package com.example.fitness_app.nutrition

import com.example.fitness_app.screens.*

/*
 * MVP-калькулятор питания
 * Использует усреднённые значения диапазонов
 */

object NutritionCalculator {

    data class Result(
        val calories: Int,
        val proteins: Int, // граммы
        val fats: Int,     // граммы
        val carbs: Int     // граммы
    )

    fun calculate(
        gender: Gender,
        age: AgeGroup,
        height: HeightGroup,
        weight: WeightGroup,
        activity: ActivityLevel,
        goal: Goal
    ): Result {

        val ageValue = age.toAvg()
        val heightValue = height.toAvg()
        val weightValue = weight.toAvg()

        // 1. BMR (Mifflin–St Jeor, упрощённо)
        val bmr = if (gender == Gender.Мужской) {
            10 * weightValue + 6.25 * heightValue - 5 * ageValue + 5
        } else {
            10 * weightValue + 6.25 * heightValue - 5 * ageValue - 161
        }

        // 2. Суточная норма калорий
        var calories = (bmr * activity.factor).toInt()

        // 3. Коррекция под цель
        calories = when (goal) {
            Goal.LOSE -> calories - 300
            Goal.MAINTAIN -> calories
            Goal.GAIN -> calories + 300
        }

        // 4. Белки (г/кг)
        val proteinPerKg = when (goal) {
            Goal.LOSE -> 2.0
            Goal.MAINTAIN -> 1.6
            Goal.GAIN -> 1.8
        }

        val proteins = (weightValue * proteinPerKg).toInt()

        // 5. Жиры (г/кг)
        val fats = (weightValue * 0.9).toInt()

        // 6. Углеводы — остаток
        val proteinCalories = proteins * 4
        val fatCalories = fats * 9
        val carbCalories = calories - (proteinCalories + fatCalories)

        val carbs = (carbCalories / 4).coerceAtLeast(0)

        return Result(
            calories = calories,
            proteins = proteins,
            fats = fats,
            carbs = carbs
        )
    }
}

/* -------------------- EXTENSIONS -------------------- */

private fun AgeGroup.toAvg(): Int = when (this) {
    AgeGroup.A9_20 -> 15
    AgeGroup.A21_35 -> 28
    AgeGroup.A36_50 -> 43
    AgeGroup.A51_PLUS -> 58
}

private fun HeightGroup.toAvg(): Int = when (this) {
    HeightGroup.H150_160 -> 155
    HeightGroup.H161_170 -> 165
    HeightGroup.H171_180 -> 175
    HeightGroup.H181_190 -> 185
    HeightGroup.H191_PLUS -> 195
}

private fun WeightGroup.toAvg(): Int = when (this) {
    WeightGroup.W40_55 -> 50
    WeightGroup.W56_70 -> 63
    WeightGroup.W71_85 -> 78
    WeightGroup.W86_100 -> 93
    WeightGroup.W101_PLUS -> 108
}
