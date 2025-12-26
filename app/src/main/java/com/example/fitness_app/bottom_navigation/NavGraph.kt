package com.example.fitness_app.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


// Функция NavGraph отвечает за описание всей навигации приложения.
// Здесь указываются все экраны и маршруты,
// между которыми пользователь может переходить.
@Composable
fun NavGraph(
    navHostController: NavHostController
) {

    // NavHost — контейнер, в котором отображаются экраны
    // в зависимости от текущего маршрута (route)
    NavHost(
        navController = navHostController,
        // Стартовый экран приложения
        // Открывается при запуске приложения
        startDestination = Home.route
    ) {
        // Главный экран приложения
        composable(Home.route) {
            Screen1()
        }
        // Экран профиля пользователя
        composable(Page2.route) {
            Screen2()
        }
        // Экран чата / помощника
        composable(Page3.route) {
            Screen3()
        }
        // Экран достижений (ачивок)
        composable(Page4.route) {
            Screen4()
        }
        // Экран с продуктами и быстрым добавлением калорий
        composable(Page5.route) {
            Screen5()
        }
    }
}