package com.example.fitness_app.feature.chat.di

import com.example.fitness_app.data.ai.gigachat.DELETGigaChatAuthApi
import com.example.fitness_app.data.ai.gigachat.DELETGigaChatChatApi
import com.example.fitness_app.data.ai.gigachat.DELETGigaChatChatRepository
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase
import com.google.gson.Gson
import okhttp3.OkHttpClient

object GigaChatFeatureProvider {

    private const val AUTHORIZATION_KEY = "NzMwMjQxY2MtYzY1OC00NDM1LWIyY2EtYzRiNWUxODBhMDRmOjhhZmU4MzEyLTQ4NmEtNGI5Zi05OTFkLTBmYWJiZGQyNzU3NA=="

    private val gson = Gson()

    private val client = OkHttpClient.Builder().build()

    private val authApi = DELETGigaChatAuthApi(
        client = client,
        gson = gson
    )

    private val chatApi = DELETGigaChatChatApi(
        client = client,
        gson = gson
    )

    private val repository = DELETGigaChatChatRepository(
        authorizationKey = AUTHORIZATION_KEY,
        authApi = authApi,
        chatApi = chatApi
    )

    val sendChatMessageUseCase = SendChatMessageUseCase(repository)
}