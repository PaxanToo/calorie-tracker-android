package com.example.fitness_app.screens

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.airbnb.lottie.compose.*
import com.example.fitness_app.R
import kotlinx.coroutines.delay



private val Context.dataStore by preferencesDataStore(name = "calories_prefs")
private val CAL_GOAL = intPreferencesKey("cal_goal")
private val CAL_EATEN = intPreferencesKey("cal_eaten")






@Composable
fun CircularProgressBar(
    percentage: Float,
    number: Int,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 60.dp,
    color: Color = Color.Green,
    strokeWidth: Dp = 8.dp,
    animDuration: Int = 1000
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val curPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(durationMillis = animDuration),
        label = "progress_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = Modifier.size(radius * 2)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * curPercentage,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = (curPercentage * number).toInt().toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ScreenHome() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var goal by remember { mutableStateOf(2200) }
    var eaten by remember { mutableStateOf(0) }

    var goalInput by remember { mutableStateOf("") }
    var eatenInput by remember { mutableStateOf("") }

    var showAchievement by remember { mutableStateOf(false) }
    var achievementShownForCurrentGoal by remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        goal = prefs[CAL_GOAL] ?: 2200
        eaten = prefs[CAL_EATEN] ?: 0
    }


    val progress = if (goal > 0) (eaten.toFloat() / goal).coerceIn(0f, 1f) else 0f


    LaunchedEffect(eaten, goal) {
        if (eaten >= goal && !achievementShownForCurrentGoal) {
            showAchievement = true
            achievementShownForCurrentGoal = true

            delay(2000) // показываем 2 секунды
            showAchievement = false
        }
    }


    Box(modifier = Modifier.fillMaxSize()){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(20.dp))

            CircularProgressBar(
                percentage = progress,
                number = goal,
                color = Color(0xFF4CAF50)


            )
            Spacer(modifier = Modifier.height(8.dp))



            Text(
                text = "$eaten / $goal ккал",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))




            OutlinedTextField(
                value = goalInput,
                onValueChange = { goalInput = it },
                label = { Text("Цель (до 5000 ккал)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))



            Button(
                onClick = {
                    val value = goalInput.toIntOrNull()
                    if (value != null && value in 1..5000) {
                        goal = value
                        goalInput = ""
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[CAL_GOAL] = goal
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Сохранить цель")
            }
            Spacer(modifier = Modifier.height(24.dp))



            OutlinedTextField(
                value = eatenInput,
                onValueChange = { eatenInput = it },
                label = { Text("Съедено калорий") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))



            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Button(
                    onClick = {
                        val value = eatenInput.toIntOrNull()
                        if (value != null && value > 0) {
                            eaten += value
                            eatenInput = ""
                            scope.launch {
                                context.dataStore.edit { preferences ->
                                    preferences[CAL_EATEN] = eaten
                                }
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Добавить")
                }

                Button(
                    onClick = {

                        achievementShownForCurrentGoal = false

                        eaten = 0
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[CAL_EATEN] = eaten
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сбросить")
                }
            }
        }


        if (showAchievement) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.lottie)
            )

            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp)
            )
        }




    }

}

@Preview(showBackground = true)
@Composable
fun ScreenHomePreview() {
    ScreenHome()
}