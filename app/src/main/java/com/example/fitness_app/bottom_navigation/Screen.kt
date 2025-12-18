package com.example.fitness_app.bottom_navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.fitness_app.screens.Screen222
import com.example.fitness_app.screens.ScreenAchievements
import com.example.fitness_app.screens.ScreenHome

@Composable
fun Screen1() {
    ScreenHome()
}

@Composable
fun Screen2() {
    Screen222()
}

@Composable
fun Screen3() {
    Text(
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        text = "page3",
        textAlign = TextAlign.Center

    )
}

@Composable
fun Screen4() {
    ScreenAchievements()
}