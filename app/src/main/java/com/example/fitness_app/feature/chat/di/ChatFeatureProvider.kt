package com.example.fitness_app.feature.chat.di

import com.example.fitness_app.data.ai.fake.FakeChatRepository
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase


object ChatFeatureProvider {
    private val chatRepository = FakeChatRepository()

    val sendChatMessageUseCase: SendChatMessageUseCase =
        SendChatMessageUseCase(chatRepository)
}