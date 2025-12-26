package com.example.fitness_app.bottom_navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController


// Аннотация подавляет предупреждение о неиспользуемом padding,
// так как отступы Scaffold обрабатываются вручную внутри экранов
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {

    // NavController отвечает за навигацию между экранами приложения
    val navController = rememberNavController()

    // Scaffold — базовый layout Material 3,
    // используется для размещения стандартных элементов интерфейса
    // (bottomBar, topBar, floatingActionButton и т.д.)
    Scaffold(
        // Нижняя навигационная панель приложения
        bottomBar = {BottomNavigationBar(navController = navController)}
    ){

        // Основной граф навигации приложения.
        // Здесь описываются все экраны и маршруты,
        // между которыми можно переходить
        NavGraph(navHostController = navController)

    }
}