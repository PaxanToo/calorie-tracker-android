package com.example.fitness_app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter



data class CalorieEntry(
    val calories: Int,
    val time: String
)



private fun encodeEntries(entries: List<CalorieEntry>): String {
    return entries.joinToString(";") { "${it.calories}|${it.time}" }
}

private fun decodeEntries(raw: String): List<CalorieEntry> {
    if (raw.isBlank()) return emptyList()

    return raw.split(";").mapNotNull {
        val parts = it.split("|")
        if (parts.size == 2) {
            CalorieEntry(
                calories = parts[0].toIntOrNull() ?: return@mapNotNull null,
                time = parts[1]
            )
        } else null
    }
}



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
        animationSpec = tween(animDuration),
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
            fontWeight = FontWeight.Bold
        )
    }
}



@Composable
fun ScreenHome() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    var goal by remember { mutableStateOf(2200) }
    var eaten by remember { mutableStateOf(0) }

    var showAchievement by remember { mutableStateOf(false) }
    var achievementShown by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    var isFabOpen by remember { mutableStateOf(false) }
    val fabProgress by animateFloatAsState(
        targetValue = if (isFabOpen) 1f else 0f,
        animationSpec = tween(400),
        label = "fab_anim"
    )

    val entries = remember { mutableStateListOf<CalorieEntry>() }



    LaunchedEffect(Unit) {
        val prefs = context.prefsDataStore().data.first()

        goal = prefs[PrefsKeys.CAL_GOAL] ?: 2200
        eaten = prefs[PrefsKeys.CAL_EATEN] ?: 0

        val saved = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
        entries.clear()
        entries.addAll(decodeEntries(saved))
    }


    val progress =
        if (goal > 0) (eaten.toFloat() / goal).coerceIn(0f, 1f) else 0f


    LaunchedEffect(eaten, goal) {
        if (eaten >= goal && !achievementShown) {
            showAchievement = true
            achievementShown = true

            delay(2000)
            showAchievement = false

            context.prefsDataStore().edit {
                it[PrefsKeys.ACH_GOAL_REACHED] = true
            }
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))


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
                    Icon(Icons.Default.Add, null)
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
                    ) { Text("+") }
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
                    ) { Text("🎯") }
                }

                if (fabProgress > 0f) {
                    FloatingActionButton(
                        onClick = {
                            isFabOpen = false
                            eaten = 0
                            achievementShown = false
                            entries.clear()

                            scope.launch {
                                context.prefsDataStore().edit {
                                    it[PrefsKeys.CAL_EATEN] = 0
                                    it[PrefsKeys.ACH_GOAL_REACHED] = false
                                    it[PrefsKeys.CAL_ENTRIES] = ""
                                }
                            }
                        },
                        modifier = Modifier
                            .offset(70.dp * fabProgress, (-10).dp * fabProgress)
                            .scale(0.7f + 0.3f * fabProgress)
                            .alpha(fabProgress),
                        containerColor = Color.Red
                    ) { Text("⟳") }
                }
            }


            Spacer(modifier = Modifier.height(52.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text("Добавленные калории", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (entries.isEmpty()) {
                        Text("Пока нет записей", color = Color.Gray)
                    } else {
                        LazyColumn {
                            items(entries) { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("+${entry.calories} ккал")
                                    Text(entry.time, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }



        if (showAddDialog) {
            SimpleInputDialog(
                title = "Добавить калории",
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    eaten += it

                    entries.add(
                        0,
                        CalorieEntry(
                            it,
                            LocalTime.now().format(timeFormatter)
                        )
                    )

                    scope.launch {
                        context.prefsDataStore().edit { prefs ->
                            prefs[PrefsKeys.CAL_EATEN] = eaten
                            prefs[PrefsKeys.CAL_ENTRIES] = encodeEntries(entries)
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
                    achievementShown = false

                    scope.launch {
                        context.prefsDataStore().edit {
                            it[PrefsKeys.CAL_GOAL] = goal
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
                composition,
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

            Column {

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Калории") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Быстро добавить:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = {
                            onConfirm(50)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+50")
                    }

                    Button(
                        onClick = {
                            onConfirm(200)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+200")
                    }

                    Button(
                        onClick = {
                            onConfirm(550)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+550")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    value.toIntOrNull()?.let {
                        onConfirm(it)
                        onDismiss()
                    }
                }
            ) {
                Text("Добавить")
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
