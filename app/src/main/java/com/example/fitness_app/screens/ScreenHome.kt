package com.example.fitness_app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.airbnb.lottie.compose.*
import com.example.fitness_app.R
import kotlinx.coroutines.delay
import com.example.fitness_app.DATA.PrefsKeys
import com.example.fitness_app.DATA.prefsDataStore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.roundToInt










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

    var showAchievement by remember { mutableStateOf(false) }
    var achievementShownForCurrentGoal by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    var isFabOpen by remember { mutableStateOf(false) }
    val fabProgress by animateFloatAsState(
        targetValue = if (isFabOpen) 1f else 0f,
        animationSpec = tween(400),
        label = "fab_anim"
    )



    LaunchedEffect(Unit) {
        val prefs = context.prefsDataStore().data.first()
        goal = prefs[PrefsKeys.CAL_GOAL] ?: 2200
        eaten = prefs[PrefsKeys.CAL_EATEN] ?: 0
    }


    val progress =
        if (goal > 0) (eaten.toFloat() / goal).coerceIn(0f, 1f) else 0f


    LaunchedEffect(eaten, goal) {
        if (eaten >= goal && !achievementShownForCurrentGoal) {
            showAchievement = true
            achievementShownForCurrentGoal = true

            delay(2000)
            showAchievement = false

            scope.launch {
                context.prefsDataStore().edit {
                    it[PrefsKeys.ACH_GOAL_REACHED] = true
                }
            }
        }
    }



    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Box(contentAlignment = Alignment.Center) {

            CircularProgressBar(
                percentage = progress,
                number = goal,
                color = Color(0xFF4CAF50)
            )

            Text(
                text = "$eaten / $goal ккал",
                modifier = Modifier.offset(y = 90.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )


            FloatingActionButton(
                onClick = { isFabOpen = !isFabOpen },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (90.dp.toPx()).roundToInt(),
                            (-40.dp.toPx()).roundToInt()
                        )
                    }
                    .rotate(225f * fabProgress)
                    .scale(1f + 0.15f * fabProgress)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }


            if (fabProgress > 0f) {
                FloatingActionButton(
                    onClick = {
                        isFabOpen = false
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .offset((-70).dp * fabProgress, (-10).dp * fabProgress)
                        .scale(0.7f + 0.3f * fabProgress)
                        .alpha(fabProgress)
                ) {
                    Text("+")
                }
            }

            if (fabProgress > 0f) {
                FloatingActionButton(
                    onClick = {
                        isFabOpen = false
                        showGoalDialog = true
                    },
                    modifier = Modifier
                        .offset(0.dp, (-80).dp * fabProgress)
                        .scale(0.7f + 0.3f * fabProgress)
                        .alpha(fabProgress)
                ) {
                    Text("🎯")
                }
            }

            if (fabProgress > 0f) {
                FloatingActionButton(
                    onClick = {
                        isFabOpen = false
                        eaten = 0
                        achievementShownForCurrentGoal = false

                        scope.launch {
                            context.prefsDataStore().edit {
                                it[PrefsKeys.CAL_EATEN] = 0
                                it[PrefsKeys.ACH_GOAL_REACHED] = false
                            }
                        }
                    },
                    modifier = Modifier
                        .offset(70.dp * fabProgress, (-10).dp * fabProgress)
                        .scale(0.7f + 0.3f * fabProgress)
                        .alpha(fabProgress),
                    containerColor = Color.Red
                ) {
                    Text("⟳")
                }
            }
        }



        if (showAddDialog) {
            SimpleInputDialog(
                title = "Добавить калории",
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    eaten += it
                    scope.launch {
                        context.prefsDataStore().edit { prefs ->
                            prefs[PrefsKeys.CAL_EATEN] = eaten
                        }
                    }
                }
            )
        }

        if (showGoalDialog) {
            SimpleInputDialog(
                title = "Новая цель",
                onDismiss = { showGoalDialog = false },
                onConfirm = {
                    goal = it
                    achievementShownForCurrentGoal = false
                    scope.launch {
                        context.prefsDataStore().edit { prefs ->
                            prefs[PrefsKeys.CAL_GOAL] = goal
                        }
                    }
                }
            )
        }



        if (showAchievement) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.lottie)
            )

            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier.size(250.dp)
            )
        }
    }
}



@Composable
fun SimpleInputDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var value by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                value.toIntOrNull()?.let {
                    onConfirm(it)
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun ScreenHomePreview() {
    ScreenHome()
}
