package com.example.fitness_app.domain.chat.usecase

import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse
import com.example.fitness_app.domain.chat.repository.ChatRepository

class SendChatMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(request: ChatRequest): ChatResponse {
        return repository.sendMessage(request)
    }
}