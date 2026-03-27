package com.example.fitness_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fitness_app.app.AppRoot
import com.example.fitness_app.app.ui.AppSplashScreen
import com.example.fitness_app.ui.theme.Fitness_appTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Fitness_appTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    AppSplashScreen(
                        onFinished = {
                            showSplash = false
                        }
                    )
                } else {
                    AppRoot()
                }
            }
        }
    }
}