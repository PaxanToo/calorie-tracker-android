package com.example.fitness_app.DATA

data class CalorieEntry(
    val calories: Int,
    val time: String
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