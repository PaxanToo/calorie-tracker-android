package com.example.fitness_app.feature.chat.presentation

import android.net.Uri
import com.example.fitness_app.feature.chat.presentation.model.ChatMessageUi
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi

data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
    val selectedMode: ChatMode = ChatMode.DEFAULT,
    val selectedGoal: NutritionGoalUi? = null,
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)