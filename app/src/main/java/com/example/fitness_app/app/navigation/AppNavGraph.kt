package com.example.fitness_app.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness_app.feature.achievements.AchievementsScreen
import com.example.fitness_app.feature.chat.ui.ChatScreen
import com.example.fitness_app.feature.food.FoodScreen
import com.example.fitness_app.feature.home.HomeScreen
import com.example.fitness_app.feature.profile.ProfileSetupScreen

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    contentPadding: PaddingValues
) {
    NavHost(
        navController = navHostController,
        startDestination = HomeDestination.route
    ) {
        composable(HomeDestination.route) {
            HomeScreen()
        }

        composable(ProfileDestination.route) {
            ProfileSetupScreen()
        }

        composable(ChatDestination.route) {
            ChatScreen(contentPadding = contentPadding)
        }

        composable(AchievementsDestination.route) {
            AchievementsScreen()
        }

        composable(FoodDestination.route) {
            FoodScreen()
        }
    }
}