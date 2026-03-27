package com.example.fitness_app.domain.model

enum class Gender {
    MALE,
    FEMALE
}

enum class AgeGroup(val label: String) {
    A9_20("9–20 лет"),
    A21_35("21–35 лет"),
    A36_50("36–50 лет"),
    A51_PLUS("51+ лет")
}

enum class HeightGroup(val label: String) {
    H150_160("150–160 см"),
    H161_170("161–170 см"),
    H171_180("171–180 см"),
    H181_190("181–190 см"),
    H191_PLUS("191+ см")
}

enum class WeightGroup(val label: String) {
    W40_55("40–55 кг"),
    W56_70("56–70 кг"),
    W71_85("71–85 кг"),
    W86_100("86–100 кг"),
    W101_PLUS("101+ кг")
}

enum class ActivityLevel(val label: String, val factor: Float) {
    LOW("Минимальная активность", 1.2f),
    LIGHT("Лёгкая активность", 1.375f),
    MEDIUM("Средняя активность", 1.55f),
    HIGH("Высокая активность", 1.725f),
    VERY_HIGH("Очень высокая", 1.9f)
}

enum class Goal(val label: String) {
    LOSE("Похудение"),
    MAINTAIN("Поддержание веса"),
    GAIN("Набор массы")
}