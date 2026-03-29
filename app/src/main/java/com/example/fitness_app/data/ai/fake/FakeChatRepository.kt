package com.example.fitness_app.data.ai.fake

import com.example.fitness_app.domain.chat.model.ChatModeDomain
import com.example.fitness_app.domain.chat.model.ChatRequest
import com.example.fitness_app.domain.chat.model.ChatResponse
import com.example.fitness_app.domain.chat.model.NutritionGoalDomain
import com.example.fitness_app.domain.chat.repository.ChatRepository
import kotlinx.coroutines.delay

class FakeChatRepository : ChatRepository {

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        delay(1200)

        val answer = when (request.mode) {
            ChatModeDomain.DEFAULT -> {
                "Это тестовый ответ AI-чата. Ты написал: \"${request.message}\"."
            }

            ChatModeDomain.MEAL_CALORIES -> {
                if (request.imageUri != null) {
                    "Похоже, это блюдо из категории основного приёма пищи. Примерная калорийность: 350–500 ккал. Позже сюда подключим реальный анализ фото."
                } else {
                    "Для оценки калорий в этом режиме лучше прикрепить фото блюда."
                }
            }

            ChatModeDomain.DISH_SUGGESTION -> {
                val goalText = when (request.selectedGoal) {
                    null -> "без указанной цели"
                    NutritionGoalDomain.LOSE_WEIGHT -> "похудение"
                    NutritionGoalDomain.MAINTAIN_WEIGHT -> "поддержание веса"
                    NutritionGoalDomain.GAIN_WEIGHT -> "набор массы"
                }

                if (request.imageUri != null) {
                    "Я вижу набор продуктов. С учётом цели \"$goalText\" можно предложить простой вариант блюда. Позже сюда подключим реальное распознавание ингредиентов."
                } else {
                    "Для подбора блюда прикрепи фото продуктов. Текущая цель: $goalText."
                }
            }
        }

        return ChatResponse(text = answer)
    }
}