package com.example.fitness_app.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.fitness_app.app.navigation.AppBottomBar
import com.example.fitness_app.app.navigation.AppFabMenu
import com.example.fitness_app.app.navigation.AppNavGraph

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController)
        },
        floatingActionButton = {
            AppFabMenu(navController = navController)
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        AppNavGraph(
            navHostController = navController,
            contentPadding = innerPadding
        )
    }
}