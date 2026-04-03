package com.example.fitness_app.data.ai.proxy

import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse
import com.example.fitness_app.domain.chat.repository.ChatRepository
import android.content.Context
import com.example.fitness_app.domain.chat.model.ChatModeDomain
import com.example.fitness_app.domain.chat.model.NutritionGoalDomain

class ProxyChatRepository(
    private val api: ProxyChatApi,
    private val context: Context
) : ChatRepository {

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        val mode = request.mode.toProxyMode()
        val goal = request.selectedGoal?.toProxyGoal()

        return if (request.imageUri != null) {
            val response = api.sendImageMessage(
                context = context,
                imageUri = request.imageUri,
                message = request.message,
                mode = mode,
                goal = goal
            )

            if (!response.success) {
                error(response.error?.toString() ?: "Proxy image request failed")
            }

            ChatResponse(
                text = response.answer ?: "Пустой ответ от proxy"
            )
        } else {
            val response = api.sendTextMessage(
                message = request.message.ifBlank {
                    "Пользователь отправил сообщение без текста"
                },
                mode = mode,
                goal = goal
            )

            if (!response.success) {
                error(response.error?.toString() ?: "Proxy text request failed")
            }

            ChatResponse(
                text = response.answer ?: "Пустой ответ от proxy"
            )
        }
    }

    private fun ChatModeDomain.toProxyMode(): String {
        return when (this) {
            ChatModeDomain.DEFAULT -> "DEFAULT"
            ChatModeDomain.MEAL_CALORIES -> "MEAL_CALORIES"
            ChatModeDomain.DISH_SUGGESTION -> "DISH_SUGGESTION"
        }
    }

    private fun NutritionGoalDomain.toProxyGoal(): String {
        return when (this) {
            NutritionGoalDomain.LOSE_WEIGHT -> "LOSE_WEIGHT"
            NutritionGoalDomain.MAINTAIN_WEIGHT -> "MAINTAIN_WEIGHT"
            NutritionGoalDomain.GAIN_WEIGHT -> "GAIN_WEIGHT"
        }
    }

}