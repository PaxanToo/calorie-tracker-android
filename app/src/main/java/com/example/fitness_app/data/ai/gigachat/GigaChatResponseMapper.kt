package com.example.fitness_app.data.ai.gigachat

import com.example.fitness_app.domain.chat.model.ChatResponse

object GigaChatResponseMapper {

    fun map(response: GigaChatMessageResponse): ChatResponse {
        val text = response.choices
            .firstOrNull()
            ?.message
            ?.content

        val finalText = if (text.isNullOrBlank()) {
            "Не удалось получить содержательный ответ от GigaChat."
        } else {
            text
        }

        return ChatResponse(
            text = finalText
        )
    }
}