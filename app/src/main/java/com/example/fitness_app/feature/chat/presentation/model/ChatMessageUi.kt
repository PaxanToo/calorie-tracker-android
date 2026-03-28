package com.example.fitness_app.feature.chat.presentation.model

data class ChatMessageUi(
    val id: Long,
    val text: String,
    val isFromUser: Boolean
)