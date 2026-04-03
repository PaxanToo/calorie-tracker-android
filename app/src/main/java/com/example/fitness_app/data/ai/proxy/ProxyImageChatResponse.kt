package com.example.fitness_app.data.ai.proxy

data class ProxyImageChatResponse(
    val success: Boolean,
    val answer: String? = null,
    val error: Any? = null
)