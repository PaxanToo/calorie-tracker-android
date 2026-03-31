package com.example.fitness_app.data.ai.gigachat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class GigaChatChatApi(
    private val client: OkHttpClient,
    private val gson: com.google.gson.Gson
) {

    fun sendMessage(
        accessToken: String,
        requestBody: GigaChatMessageRequest
    ): GigaChatMessageResponse {
        val jsonBody = gson.toJson(requestBody)

        val request = Request.Builder()
            .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Chat request failed: ${response.code} ${response.message}")
            }

            val body = response.body?.string()
                ?: error("Chat response body is empty")

            return gson.fromJson(body, GigaChatMessageResponse::class.java)
        }
    }
}