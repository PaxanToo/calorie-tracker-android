package com.example.fitness_app.data.ai.proxy

import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse
import com.example.fitness_app.domain.chat.repository.ChatRepository

class ProxyChatRepository(
    private val api: ProxyChatApi
) : ChatRepository {

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        val response = api.sendTextMessage(
            message = request.message.ifBlank {
                "Пользователь отправил сообщение без текста"
            }
        )

        if (!response.success) {
            error(response.error?.toString() ?: "Proxy request failed")
        }

        return ChatResponse(
            text = response.answer ?: "Пустой ответ от proxy"
        )
    }
}