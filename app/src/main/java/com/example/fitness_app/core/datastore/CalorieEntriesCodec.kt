package com.example.fitness_app.core.datastore

data class CalorieEntry(
    val calories: Int,
    val time: String
)

data class DailyProgress(
    val date: String,
    val eatenCalories: Int,
    val goalCalories: Int,
    val goalReached: Boolean
)

fun encodeEntries(entries: List<CalorieEntry>): String =
    entries.joinToString(";") { "${it.calories}|${it.time}" }

fun decodeEntries(raw: String): List<CalorieEntry> {
    if (raw.isBlank()) return emptyList()
    return raw.split(";").mapNotNull { token ->
        val parts = token.split("|")
        if (parts.size != 2) return@mapNotNull null
        val cal = parts[0].toIntOrNull() ?: return@mapNotNull null
        CalorieEntry(calories = cal, time = parts[1])
    }
}

fun encodeDailyProgressList(items: List<DailyProgress>): String =
    items.joinToString(";") {
        "${it.date}|${it.eatenCalories}|${it.goalCalories}|${it.goalReached}"
    }

fun decodeDailyProgressList(raw: String): List<DailyProgress> {
    if (raw.isBlank()) return emptyList()

    return raw.split(";").mapNotNull { token ->
        val parts = token.split("|")
        if (parts.size != 4) return@mapNotNull null

        val date = parts[0]
        val eatenCalories = parts[1].toIntOrNull() ?: return@mapNotNull null
        val goalCalories = parts[2].toIntOrNull() ?: return@mapNotNull null
        val goalReached = parts[3].toBooleanStrictOrNull() ?: return@mapNotNull null

        DailyProgress(
            date = date,
            eatenCalories = eatenCalories,
            goalCalories = goalCalories,
            goalReached = goalReached
        )
    }
}