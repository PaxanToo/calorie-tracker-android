package com.example.fitness_app.data.ai.gigachat

import com.example.fitness_app.domain.chat.model.ChatRequest

object GigaChatRequestMapper {

    fun map(
        request: ChatRequest,
        attachmentIds: List<String>? = null
    ): GigaChatMessageRequest {
        val prompt = GigaChatPromptBuilder.buildPrompt(
            mode = request.mode,
            message = request.message,
            selectedGoal = request.selectedGoal
        )

        val userMessage = GigaChatMessage(
            role = "user",
            content = prompt,
            attachments = attachmentIds
        )

        return GigaChatMessageRequest(
            model = "GigaChat",
            messages = listOf(userMessage),
            stream = false
        )
    }
}