package com.example.fitness_app.bottom_navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.fitness_app.chat.ScreenChat
import com.example.fitness_app.food.ScreenFood
import com.example.fitness_app.screens.Screen222
import com.example.fitness_app.screens.ScreenAchievements
import com.example.fitness_app.screens.ScreenHome


// Screen1 — обёртка для главного экрана приложения.
// Используется в NavGraph для маршрута Home.route
@Composable
fun Screen1() {
    ScreenHome()
}

// Screen2 — экран профиля пользователя.
// Здесь в будущем можно расширять логику профиля,
// не трогая навигацию
@Composable
fun Screen2() {
    Screen222()
}

// Screen3 — экран чата / помощника.
// Отдельный экран для общения пользователя с приложением
@Composable
fun Screen3() {
    ScreenChat()
}

// Screen4 — экран достижений (ачивок).
// Отвечает за отображение прогресса пользователя
@Composable
fun Screen4() {
    ScreenAchievements()
}

// Screen5 — экран с продуктами питания.
// Используется для быстрого добавления калорий из базы продуктов
@Composable
fun Screen5() {
    ScreenFood()
}