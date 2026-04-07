package com.example.fitness_app.feature.chat.presentation.model

import android.net.Uri


data class NutritionInfoUi(
    val calories: Int,
    val proteins: Int,
    val fats: Int,
    val carbs: Int
)

data class ChatMessageUi(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val imageUri: Uri? = null,
    val nutritionInfo: NutritionInfoUi? = null,
    val canAddToDiary: Boolean = false,
    val isAddedToDiary: Boolean = false
)