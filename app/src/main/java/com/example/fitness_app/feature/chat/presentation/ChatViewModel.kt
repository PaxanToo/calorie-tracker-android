package com.example.fitness_app.feature.chat.presentation

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_app.core.datastore.CalorieEntry
import com.example.fitness_app.core.datastore.DailyProgress
import com.example.fitness_app.core.datastore.PrefsKeys
import com.example.fitness_app.core.datastore.decodeDailyProgressList
import com.example.fitness_app.core.datastore.decodeEntries
import com.example.fitness_app.core.datastore.encodeDailyProgressList
import com.example.fitness_app.core.datastore.encodeEntries
import com.example.fitness_app.core.datastore.prefsDataStore
import com.example.fitness_app.domain.chat.model.ChatModeDomain
import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.NutritionGoalDomain
import com.example.fitness_app.feature.chat.di.ProxyChatFeatureProvider
import com.example.fitness_app.feature.chat.presentation.model.ChatMessageUi
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi
import com.example.fitness_app.feature.chat.presentation.model.NutritionInfoUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sendChatMessageUseCase =
        ProxyChatFeatureProvider.provideSendChatMessageUseCase(application)

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMessageUi(
                    id = System.currentTimeMillis(),
                    text = "Привет! Я помогу посчитать калории блюда или предложить, что приготовить.",
                    isFromUser = false
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.InputChanged -> {
                _uiState.value = _uiState.value.copy(
                    inputText = action.value
                )
            }

            is ChatAction.ModeSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedMode = action.mode
                )
            }

            is ChatAction.GoalSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedGoal = action.goal
                )
            }

            is ChatAction.ImageSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedImageUri = action.uri
                )
            }

            is ChatAction.AddNutritionToDiary -> {
                addNutritionToDiary(action.messageId)
            }

            ChatAction.ClearSelectedImage -> {
                _uiState.value = _uiState.value.copy(
                    selectedImageUri = null
                )
            }

            ChatAction.ClearError -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = null
                )
            }

            ChatAction.SendMessage -> {
                sendMessage()
            }

            ChatAction.ConsumeAchievementAnimation -> {
                _uiState.value = _uiState.value.copy(
                    showAchievementAnimation = false
                )
            }
        }
    }

    private fun sendMessage() {
        val currentState = _uiState.value
        val messageText = currentState.inputText.trim()

        if (messageText.isEmpty() && currentState.selectedImageUri == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Введите сообщение или выберите изображение."
            )
            return
        }

        val userMessage = ChatMessageUi(
            id = System.currentTimeMillis(),
            text = buildUserMessagePreview(currentState),
            isFromUser = true,
            imageUri = currentState.selectedImageUri
        )

        _uiState.value = currentState.copy(
            messages = currentState.messages + userMessage,
            inputText = "",
            isLoading = true,
            errorMessage = null
        )

        val request = ChatRequest(
            message = messageText,
            mode = currentState.selectedMode.toDomain(),
            selectedGoal = currentState.selectedGoal?.toDomain(),
            imageUri = currentState.selectedImageUri
        )

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    sendChatMessageUseCase(request)
                }
            }.onSuccess { response ->
                val parsedNutrition = if (currentState.selectedMode == ChatMode.MEAL_CALORIES) {
                    parseNutritionFromText(response.text)
                } else {
                    null
                }

                val aiMessage = ChatMessageUi(
                    id = System.currentTimeMillis() + 1,
                    text = response.text,
                    isFromUser = false,
                    nutritionInfo = parsedNutrition,
                    canAddToDiary = parsedNutrition != null
                )

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMessage,
                    isLoading = false,
                    selectedImageUri = null
                )
            }.onFailure { throwable ->
                android.util.Log.e("GigaChat", "sendMessage failed", throwable)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = throwable.message
                        ?: "Не удалось получить ответ. Попробуйте снова."
                )
            }
        }
    }

    private fun addNutritionToDiary(messageId: Long) {
        val message = _uiState.value.messages.firstOrNull { it.id == messageId } ?: return
        val nutrition = message.nutritionInfo ?: return
        if (message.isAddedToDiary) return

        val context = getApplication<Application>().applicationContext
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        viewModelScope.launch(Dispatchers.IO) {
            val prefs = context.prefsDataStore().data.first()

            val wasAiAchievementUnlocked = prefs[PrefsKeys.ACH_AI_MEAL_ADDED] ?: false

            val currentCalories = prefs[PrefsKeys.CAL_EATEN] ?: 0
            val currentProteins = prefs[PrefsKeys.PROTEIN_EATEN] ?: 0
            val currentFats = prefs[PrefsKeys.FAT_EATEN] ?: 0
            val currentCarbs = prefs[PrefsKeys.CARBS_EATEN] ?: 0
            val currentGoal = prefs[PrefsKeys.CAL_GOAL] ?: 2200

            val newCalories = currentCalories + nutrition.calories
            val newProteins = currentProteins + nutrition.proteins
            val newFats = currentFats + nutrition.fats
            val newCarbs = currentCarbs + nutrition.carbs

            val savedEntriesRaw = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
            val entries = decodeEntries(savedEntriesRaw).toMutableList()

            entries.add(
                0,
                CalorieEntry(
                    calories = nutrition.calories,
                    time = LocalTime.now().format(timeFormatter)
                )
            )

            val today = LocalDate.now().toString()
            val history = decodeDailyProgressList(
                prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
            ).toMutableList()

            val updatedItem = DailyProgress(
                date = today,
                eatenCalories = newCalories,
                goalCalories = currentGoal,
                goalReached = newCalories >= currentGoal
            )

            val existingIndex = history.indexOfFirst { it.date == today }
            if (existingIndex >= 0) {
                history[existingIndex] = updatedItem
            } else {
                history.add(updatedItem)
            }

            context.prefsDataStore().edit { editPrefs ->
                editPrefs[PrefsKeys.CAL_EATEN] = newCalories
                editPrefs[PrefsKeys.PROTEIN_EATEN] = newProteins
                editPrefs[PrefsKeys.FAT_EATEN] = newFats
                editPrefs[PrefsKeys.CARBS_EATEN] = newCarbs
                editPrefs[PrefsKeys.CAL_ENTRIES] = encodeEntries(entries)
                editPrefs[PrefsKeys.DAILY_PROGRESS_HISTORY] =
                    encodeDailyProgressList(history.sortedBy { it.date })

                editPrefs[PrefsKeys.ACH_AI_MEAL_ADDED] = true
            }

            val updatedMessages = _uiState.value.messages.map {
                if (it.id == messageId) {
                    it.copy(isAddedToDiary = true)
                } else {
                    it
                }
            }

            _uiState.value = _uiState.value.copy(
                messages = updatedMessages,
                showAchievementAnimation = !wasAiAchievementUnlocked
            )
        }
    }

    private fun parseNutritionFromText(text: String): NutritionInfoUi? {
        val calories = extractInt(text, "Калории")
        val proteins = extractInt(text, "Белки")
        val fats = extractInt(text, "Жиры")
        val carbs = extractInt(text, "Углеводы")

        return if (
            calories != null &&
            proteins != null &&
            fats != null &&
            carbs != null
        ) {
            NutritionInfoUi(
                calories = calories,
                proteins = proteins,
                fats = fats,
                carbs = carbs
            )
        } else {
            null
        }
    }

    private fun extractInt(text: String, label: String): Int? {
        val regex = Regex("""$label:\s*~?\s*(\d+(?:[.,]\d+)?)""", RegexOption.IGNORE_CASE)
        val match = regex.find(text) ?: return null
        val rawValue = match.groupValues.getOrNull(1)?.replace(",", ".") ?: return null
        return rawValue.toFloatOrNull()?.roundToInt()
    }

    private fun buildUserMessagePreview(state: ChatUiState): String {
        val text = state.inputText.trim()

        return when {
            text.isNotBlank() -> text
            state.selectedImageUri != null -> "Фото"
            else -> ""
        }
    }
}

private fun ChatMode.toDomain(): ChatModeDomain {
    return when (this) {
        ChatMode.DEFAULT -> ChatModeDomain.DEFAULT
        ChatMode.MEAL_CALORIES -> ChatModeDomain.MEAL_CALORIES
        ChatMode.DISH_SUGGESTION -> ChatModeDomain.DISH_SUGGESTION
    }
}

private fun NutritionGoalUi.toDomain(): NutritionGoalDomain {
    return when (this) {
        NutritionGoalUi.LOSE_WEIGHT -> NutritionGoalDomain.LOSE_WEIGHT
        NutritionGoalUi.MAINTAIN_WEIGHT -> NutritionGoalDomain.MAINTAIN_WEIGHT
        NutritionGoalUi.GAIN_WEIGHT -> NutritionGoalDomain.GAIN_WEIGHT
    }
}