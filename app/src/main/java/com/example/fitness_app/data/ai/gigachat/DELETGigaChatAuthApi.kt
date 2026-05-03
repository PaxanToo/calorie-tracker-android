package com.example.fitness_app.data.ai.gigachat


import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

class DELETGigaChatAuthApi(
    private val client: OkHttpClient,
    private val gson: com.google.gson.Gson
) {

    fun getAccessToken(
        authorizationKey: String
    ): GigaChatTokenResponse {
        val requestBody = FormBody.Builder()
            .add("scope", "GIGACHAT_API_PERS")
            .build()

        val request = Request.Builder()
            .url("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "application/json")
            .addHeader("RqUID", UUID.randomUUID().toString())
            .addHeader("Authorization", "Basic $authorizationKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Token request failed: ${response.code} ${response.message}")
            }

            val body = response.body?.string()
                ?: error("Token response body is empty")

            return gson.fromJson(body, GigaChatTokenResponse::class.java)
        }
    }
}