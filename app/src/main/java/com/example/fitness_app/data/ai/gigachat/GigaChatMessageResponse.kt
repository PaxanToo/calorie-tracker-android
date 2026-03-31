package com.example.fitness_app.data.ai.gigachat

import com.google.gson.annotations.SerializedName

data class GigaChatMessageResponse(
    val choices: List<GigaChatChoice> = emptyList()
)

data class GigaChatChoice(
    val message: GigaChatAnswerMessage? = null,
    val index: Int = 0,
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class GigaChatAnswerMessage(
    val content: String? = null,
    val role: String? = null
)