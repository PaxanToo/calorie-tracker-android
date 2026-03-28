package com.example.fitness_app.feature.chat.presentation

import android.net.Uri
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi

sealed interface ChatAction {
    data class InputChanged(val value: String) : ChatAction
    data class ModeSelected(val mode: ChatMode) : ChatAction
    data class GoalSelected(val goal: NutritionGoalUi) : ChatAction
    data class ImageSelected(val uri: Uri?) : ChatAction
    data object SendMessage : ChatAction
    data object ClearSelectedImage : ChatAction
    data object ClearError : ChatAction
}