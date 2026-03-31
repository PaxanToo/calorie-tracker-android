package com.example.fitness_app.data.ai.gigachat

import com.example.fitness_app.domain.chat.model.ChatModeDomain
import com.example.fitness_app.domain.chat.model.NutritionGoalDomain

object GigaChatPromptBuilder {

    fun buildPrompt(
        mode: ChatModeDomain,
        message: String,
        selectedGoal: NutritionGoalDomain?
    ): String {
        return when (mode) {
            ChatModeDomain.DEFAULT -> buildDefaultPrompt(message)
            ChatModeDomain.MEAL_CALORIES -> buildMealCaloriesPrompt(message)
            ChatModeDomain.DISH_SUGGESTION -> buildDishSuggestionPrompt(
                message = message,
                selectedGoal = selectedGoal
            )
        }
    }

    private fun buildDefaultPrompt(message: String): String {
        return """
            Ты — помощник в приложении для контроля питания.
            Отвечай кратко, понятно и по делу.
            
            Сообщение пользователя:
            $message
        """.trimIndent()
    }

    private fun buildMealCaloriesPrompt(message: String): String {
        return """
            Ты — помощник по анализу питания.
            Пользователь отправил изображение блюда.
            Определи, что это может быть за блюдо, и оцени примерную калорийность.
            Если точность ограничена, прямо скажи, что оценка примерная.
            Ответ должен быть понятным и коротким.
            
            Дополнительный текст пользователя:
            $message
        """.trimIndent()
    }

    private fun buildDishSuggestionPrompt(
        message: String,
        selectedGoal: NutritionGoalDomain?
    ): String {
        val goalText = when (selectedGoal) {
            NutritionGoalDomain.LOSE_WEIGHT -> "похудение"
            NutritionGoalDomain.MAINTAIN_WEIGHT -> "поддержание веса"
            NutritionGoalDomain.GAIN_WEIGHT -> "набор массы"
            null -> "без конкретной цели"
        }

        return """
            Ты — помощник по питанию.
            Пользователь отправил изображение набора продуктов.
            Определи, какие продукты видны на изображении, и предложи, что можно из них приготовить.
            Учитывай цель пользователя: $goalText.
            Отвечай понятно, практично и без лишней воды.
            
            Дополнительный текст пользователя:
            $message
        """.trimIndent()
    }
}