package com.example.fitness_app.data.ai.gigachat

import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse
import com.example.fitness_app.domain.chat.repository.ChatRepository

class DELETGigaChatChatRepository(
    private val authorizationKey: String,
    private val authApi: DELETGigaChatAuthApi,
    private val chatApi: DELETGigaChatChatApi
) : ChatRepository {

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        val tokenResponse = authApi.getAccessToken(
            authorizationKey = authorizationKey
        )

        val gigaRequest = GigaChatRequestMapper.map(
            request = request,
            attachmentIds = null
        )

        val gigaResponse = chatApi.sendMessage(
            accessToken = tokenResponse.accessToken,
            requestBody = gigaRequest
        )

        return GigaChatResponseMapper.map(gigaResponse)
    }
}