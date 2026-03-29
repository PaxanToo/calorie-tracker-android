package com.example.fitness_app.domain.chat.repository

import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse

interface ChatRepository {
    suspend fun sendMessage(request: ChatRequest): ChatResponse
}