package com.example.fitness_app.data.ai.proxy

data class ProxyChatRequest(
    val message: String,
    val mode: String,
    val goal: String? = null
)