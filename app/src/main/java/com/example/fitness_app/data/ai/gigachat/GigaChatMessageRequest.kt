package com.example.fitness_app.data.ai.gigachat

data class GigaChatMessageRequest(
    val model: String,
    val messages: List<GigaChatMessage>,
    val stream: Boolean = false
)

data class GigaChatMessage(
    val role: String,
    val content: String,
    val attachments: List<String>? = null
)