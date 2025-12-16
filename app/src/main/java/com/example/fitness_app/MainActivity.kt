package com.example.fitness_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitness_app.ui.theme.Fitness_appTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
           /* Column{ // можно делать контейнеры как и лейауты
                Text(text = "Hello")
                Text(text = "Heasdllo")
                Text(text = "Hello123")
                Text(text = "Hello")
                Text(text = "Hsdasello222222")
            }
         */
          /*  Row {
                Text(text = "Hello")
                Text(text = "Heasdllo")
                Text(text = "Hello123")
                Text(text = "Hello")
                Text(text = "Hsdasello222222")фывфыв
            } */

            Row(
                modifier = Modifier.
            background(Color.Red).fillMaxSize()
            )
            {
                Text(text = "Hello")
                Text(text = "Heasdllo")
                Text(text = "Hello123")
                Text(text = "Hello")
                Text(text = "Hsdasello222222")
            }


            }
        }
    }




