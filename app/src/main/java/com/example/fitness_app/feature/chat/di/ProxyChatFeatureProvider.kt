package com.example.fitness_app.feature.chat.di

import com.example.fitness_app.data.ai.proxy.ProxyChatApi
import com.example.fitness_app.data.ai.proxy.ProxyChatRepository
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import android.app.Application

object ProxyChatFeatureProvider {

    private const val BASE_URL = "http://192.168.0.47:3000"

    private val gson = Gson()

    private val client = OkHttpClient.Builder().build()

    private val api = ProxyChatApi(
        client = client,
        gson = gson,
        baseUrl = BASE_URL
    )

    fun provideSendChatMessageUseCase(
        application: Application
    ): SendChatMessageUseCase {
        val api = ProxyChatApi(
            client = client,
            gson = gson,
            baseUrl = BASE_URL
        )

        val repository = ProxyChatRepository(
            context = application.applicationContext,
            api = api
        )

        return SendChatMessageUseCase(repository)
    }


}