package com.example.fitness_app.domain.nutrition

import com.example.fitness_app.domain.model.ActivityLevel
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import kotlin.math.roundToInt

object NutritionCalculator {

    data class Result(
        val calories: Int,
        val proteins: Int,
        val fats: Int,
        val carbs: Int
    )

    fun calculate(
        gender: Gender,
        age: Int,
        height: Int,
        weight: Int,
        activity: ActivityLevel,
        goal: Goal
    ): Result {
        val bmr = if (gender == Gender.MALE) {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }

        val tdee = bmr * activity.factor

        val calories = when (goal) {
            Goal.LOSE -> tdee * 0.85
            Goal.MAINTAIN -> tdee
            Goal.GAIN -> tdee * 1.10
        }.roundToInt()

        val proteinPerKg = when (goal) {
            Goal.LOSE -> 2.0
            Goal.MAINTAIN -> 1.6
            Goal.GAIN -> 1.8
        }

        val fatPerKg = 0.9

        val proteins = (weight * proteinPerKg).roundToInt()
        val fats = (weight * fatPerKg).roundToInt()

        val proteinCalories = proteins * 4
        val fatCalories = fats * 9
        val carbCalories = calories - proteinCalories - fatCalories

        val carbs = (carbCalories / 4).coerceAtLeast(0)

        return Result(
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs
        )
    }
}