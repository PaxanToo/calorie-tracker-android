package com.example.fitness_app.bottom_navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun NavGraph(
    navHostController: NavHostController
) {
    NavHost(
        navController = navHostController,
        startDestination = Home.route
    ) {
        composable(Home.route) {
            Screen1()
        }
        composable(Page2.route) {
            Screen2()
        }
        composable(Page3.route) {
            Screen3()
        }
        composable(Page4.route) {
            Screen4()
        }
    }
}