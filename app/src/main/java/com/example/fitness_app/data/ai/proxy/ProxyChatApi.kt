package com.example.fitness_app.data.ai.proxy

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ProxyChatApi(
    private val client: OkHttpClient,
    private val gson: Gson,
    private val baseUrl: String
) {

    fun sendTextMessage(message: String): ProxyChatResponse {
        val requestModel = ProxyChatRequest(message = message)
        val jsonBody = gson.toJson(requestModel)

        val request = Request.Builder()
            .url("$baseUrl/api/chat/text")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
                ?: error("Proxy response body is empty")

            return gson.fromJson(body, ProxyChatResponse::class.java)
        }
    }
}