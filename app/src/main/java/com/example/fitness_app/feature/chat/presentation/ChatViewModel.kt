package com.example.fitness_app.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness_app.data.ai.fake.FakeChatRepository
import com.example.fitness_app.domain.chat.model.ChatModeDomain
import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.NutritionGoalDomain
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase
import com.example.fitness_app.feature.chat.presentation.model.ChatMessageUi
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.fitness_app.feature.chat.di.ChatFeatureProvider



class ChatViewModel : ViewModel() {

    private val sendChatMessageUseCase =ChatFeatureProvider.sendChatMessageUseCase


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
                sendChatMessageUseCase(request)
            }.onSuccess { response ->
                val aiMessage = ChatMessageUi(
                    id = System.currentTimeMillis() + 1,
                    text = response.text,
                    isFromUser = false
                )

                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMessage,
                    isLoading = false,
                    selectedImageUri = null
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Не удалось получить ответ. Попробуйте снова."
                )
            }
        }
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