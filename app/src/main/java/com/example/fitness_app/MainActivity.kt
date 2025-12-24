package com.example.fitness_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fitness_app.bottom_navigation.MainScreen
import com.example.fitness_app.ui.theme.Fitness_appTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var keepSplash = true
        splashScreen.setKeepOnScreenCondition {
            keepSplash
        }
        lifecycleScope.launch {
            delay(1000)
            keepSplash = false
        }


        enableEdgeToEdge()
        setContent {
            Fitness_appTheme {
                MainScreen()

            }
        }
    }
}


