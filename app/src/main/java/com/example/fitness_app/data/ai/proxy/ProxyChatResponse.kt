package com.example.fitness_app.data.ai.proxy

data class ProxyChatResponse(
    val success: Boolean,
    val answer: String? = null,
    val error: Any? = null
)