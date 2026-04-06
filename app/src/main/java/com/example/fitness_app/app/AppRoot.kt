package com.example.fitness_app.app

import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitness_app.app.navigation.AppBottomBar
import com.example.fitness_app.app.navigation.AppFabMenu
import com.example.fitness_app.app.navigation.AppNavGraph
import com.example.fitness_app.app.navigation.PROFILE_SETUP_ROUTE

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val hideNavigationChrome = currentRoute == PROFILE_SETUP_ROUTE

    Scaffold(
        bottomBar = {
            if (!hideNavigationChrome) {
                AppBottomBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (!hideNavigationChrome) {
                AppFabMenu(navController = navController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        AppNavGraph(
            navHostController = navController,
            contentPadding = innerPadding
        )
    }
}