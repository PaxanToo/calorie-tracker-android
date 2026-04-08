package com.example.fitness_app.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitness_app.core.datastore.hasProfileFlow
import com.example.fitness_app.feature.achievements.AchievementsScreen
import com.example.fitness_app.feature.chat.ui.ChatScreen
import com.example.fitness_app.feature.food.FoodScreen
import com.example.fitness_app.feature.home.HomeScreen
import com.example.fitness_app.feature.profile.ProfileScreen
import com.example.fitness_app.feature.profile.ProfileSetupScreen
import kotlinx.coroutines.flow.map

const val PROFILE_SETUP_ROUTE = "profile_setup"

@Composable
fun AppNavGraph(
    navHostController: NavHostController,
    contentPadding: PaddingValues
) {
    val context = LocalContext.current

    val hasProfile: Boolean? by context.hasProfileFlow()
        .map<Boolean, Boolean?> { it }
        .collectAsState(initial = null)

    if (hasProfile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (hasProfile == true) {
        HomeDestination.route
    } else {
        PROFILE_SETUP_ROUTE
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable(HomeDestination.route) {
            HomeScreen()
        }

        composable(ProfileDestination.route) {
            ProfileScreen(
                onEditClick = {
                    navHostController.navigate(PROFILE_SETUP_ROUTE)
                },
                onProfileDeleted = {
                    navHostController.navigate(PROFILE_SETUP_ROUTE) {
                        popUpTo(ProfileDestination.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(PROFILE_SETUP_ROUTE) {
            ProfileSetupScreen(
                onSaved = {
                    if (hasProfile == true) {
                        navHostController.popBackStack()
                    } else {
                        navHostController.navigate(HomeDestination.route) {
                            popUpTo(PROFILE_SETUP_ROUTE) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
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