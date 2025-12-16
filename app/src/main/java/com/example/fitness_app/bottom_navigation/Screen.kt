package com.example.fitness_app.bottom_navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun Screen1() {
    Text(
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        text = "Home",
        textAlign = TextAlign.Center

    )
}

@Composable
fun Screen2() {
    Text(
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        text = "page2",
        textAlign = TextAlign.Center

    )
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
    Text(
        modifier = Modifier.fillMaxSize().wrapContentHeight(),
        text = "page4",
        textAlign = TextAlign.Center

    )
}