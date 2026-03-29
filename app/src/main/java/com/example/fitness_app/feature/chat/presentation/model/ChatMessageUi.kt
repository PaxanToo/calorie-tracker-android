package com.example.fitness_app.feature.chat.presentation.model

import android.net.Uri

data class ChatMessageUi(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
    val imageUri: Uri? = null
)