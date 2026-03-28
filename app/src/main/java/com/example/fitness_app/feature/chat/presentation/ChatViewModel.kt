package com.example.fitness_app.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_app.feature.chat.presentation.model.ChatMessageUi
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

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
                _uiState.value = _uiState.value.copy(inputText = action.value)
            }

            is ChatAction.ModeSelected -> {
                _uiState.value = _uiState.value.copy(selectedMode = action.mode)
            }

            is ChatAction.GoalSelected -> {
                _uiState.value = _uiState.value.copy(selectedGoal = action.goal)
            }

            is ChatAction.ImageSelected -> {
                _uiState.value = _uiState.value.copy(selectedImageUri = action.uri)
            }

            ChatAction.ClearSelectedImage -> {
                _uiState.value = _uiState.value.copy(selectedImageUri = null)
            }

            ChatAction.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }

            ChatAction.SendMessage -> {
                sendMessage()
            }
        }
    }

    private fun sendMessage() {
        val state = _uiState.value
        val messageText = state.inputText.trim()

        if (messageText.isEmpty() && state.selectedImageUri == null) {
            _uiState.value = state.copy(
                errorMessage = "Введите сообщение или выберите изображение."
            )
            return
        }

        val userMessage = ChatMessageUi(
            id = System.currentTimeMillis(),
            text = buildUserMessagePreview(state),
            isFromUser = true
        )

        _uiState.value = state.copy(
            messages = state.messages + userMessage,
            inputText = "",
            isLoading = true
        )

        viewModelScope.launch {
            delay(1200)

            val answer = buildFakeAiAnswer(
                mode = state.selectedMode,
                input = messageText,
                selectedGoal = state.selectedGoal,
                hasImage = state.selectedImageUri != null
            )

            val aiMessage = ChatMessageUi(
                id = System.currentTimeMillis() + 1,
                text = answer,
                isFromUser = false
            )

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + aiMessage,
                isLoading = false,
                selectedImageUri = null
            )
        }
    }

    private fun buildUserMessagePreview(state: ChatUiState): String {
        val textPart = state.inputText.trim()
        val imagePart = if (state.selectedImageUri != null) "[изображение прикреплено]" else ""

        return listOf(textPart, imagePart)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .ifBlank { "Отправлено изображение" }
    }

    private fun buildFakeAiAnswer(
        mode: ChatMode,
        input: String,
        selectedGoal: NutritionGoalUi?,
        hasImage: Boolean
    ): String {
        return when (mode) {
            ChatMode.DEFAULT -> {
                "Это тестовый ответ AI-чата. Ты написал: \"$input\"."
            }

            ChatMode.MEAL_CALORIES -> {
                if (hasImage) {
                    "Похоже, это блюдо из категории основного приёма пищи. Примерная калорийность: 350–500 ккал. Позже сюда подключим реальный анализ фото."
                } else {
                    "Для оценки калорий в этом режиме лучше прикрепить фото блюда."
                }
            }

            ChatMode.DISH_SUGGESTION -> {
                val goalText = selectedGoal?.label ?: "без указанной цели"

                if (hasImage) {
                    "Я вижу набор продуктов. С учётом цели \"$goalText\" можно предложить простой вариант блюда. Позже сюда подключим реальное распознавание ингредиентов."
                } else {
                    "Для подбора блюда прикрепи фото продуктов. Текущая цель: $goalText."
                }
            }
        }
    }
}