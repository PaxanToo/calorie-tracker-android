package com.example.fitness_app.feature.chat.di

import com.example.fitness_app.data.ai.fake.DELETFakeChatRepository
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase


object ChatFeatureProvider {
    private val chatRepository = DELETFakeChatRepository()

    val sendChatMessageUseCase: SendChatMessageUseCase =
        SendChatMessageUseCase(chatRepository)
}