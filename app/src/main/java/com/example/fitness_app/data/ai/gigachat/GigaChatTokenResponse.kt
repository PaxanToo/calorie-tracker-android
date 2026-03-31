package com.example.fitness_app.data.ai.gigachat

import com.google.gson.annotations.SerializedName

data class GigaChatTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_at")
    val expiresAt: Long
)