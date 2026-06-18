package com.example.fitness_app.feature.chat.di

import com.example.fitness_app.data.ai.proxy.ProxyChatApi
import com.example.fitness_app.data.ai.proxy.ProxyChatRepository
import com.example.fitness_app.domain.chat.usecase.SendChatMessageUseCase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import android.app.Application

object ProxyChatFeatureProvider {

    private const val BASE_URL = "https://12418ca8-75ce-4e3d-aee9-7cfe1d62e811.tunnel4.com"

    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(180, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(180, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(180, java.util.concurrent.TimeUnit.SECONDS)
        .callTimeout(180, java.util.concurrent.TimeUnit.SECONDS)
        .build()

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