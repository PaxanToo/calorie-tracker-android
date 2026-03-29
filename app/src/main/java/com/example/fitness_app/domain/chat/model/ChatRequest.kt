package com.example.fitness_app.domain.chat.model

import android.net.Uri

data class ChatRequest(
    val message: String,
    val mode: ChatModeDomain,
    val selectedGoal: NutritionGoalDomain?,
    val imageUri: Uri?
)