package com.example.fitness_app.feature.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.datastore.preferences.core.edit
import com.airbnb.lottie.compose.*
import com.example.fitness_app.R
import com.example.fitness_app.core.datastore.CalorieEntry
import com.example.fitness_app.core.datastore.DailyProgress
import com.example.fitness_app.core.datastore.PrefsKeys
import com.example.fitness_app.core.datastore.decodeDailyProgressList
import com.example.fitness_app.core.datastore.decodeEntries
import com.example.fitness_app.core.datastore.encodeDailyProgressList
import com.example.fitness_app.core.datastore.encodeEntries
import com.example.fitness_app.core.datastore.prefsDataStore
import com.example.fitness_app.core.datastore.saveUserProfile
import com.example.fitness_app.core.datastore.userProfileFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import androidx.compose.foundation.clickable


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
fun HomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }

    val profile by context.userProfileFlow().collectAsState(initial = null)

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    var goal by remember { mutableStateOf(2200) }
    var eaten by remember { mutableStateOf(0) }
    var proteinEaten by remember { mutableStateOf(0) }
    var fatEaten by remember { mutableStateOf(0) }
    var carbsEaten by remember { mutableStateOf(0) }

    var showAchievement by remember { mutableStateOf(false) }
    var achievementShown by remember { mutableStateOf(false) }
    var shownGoalCompletionMilestone by remember { mutableStateOf<Int?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    var isFabOpen by remember { mutableStateOf(false) }

    val fabProgress by animateFloatAsState(
        targetValue = if (isFabOpen) 1f else 0f,
        animationSpec = tween(400),
        label = "fab_anim"
    )

    val entries = remember { mutableStateListOf<CalorieEntry>() }

    LaunchedEffect(profile) {
        val today = LocalDate.now().toString()

        context.prefsDataStore().edit { prefs ->
            val lastActiveDate = prefs[PrefsKeys.LAST_ACTIVE_DATE]

            when {
                lastActiveDate == null -> {
                    prefs[PrefsKeys.LAST_ACTIVE_DATE] = today
                }

                lastActiveDate != today -> {
                    prefs[PrefsKeys.CAL_EATEN] = 0
                    prefs[PrefsKeys.PROTEIN_EATEN] = 0
                    prefs[PrefsKeys.FAT_EATEN] = 0
                    prefs[PrefsKeys.CARBS_EATEN] = 0
                    prefs[PrefsKeys.CAL_ENTRIES] = ""
                    prefs[PrefsKeys.ACH_GOAL_REACHED] = false
                    prefs[PrefsKeys.LAST_ACTIVE_DATE] = today
                }
            }
        }

        val prefs = context.prefsDataStore().data.first()

        goal = prefs[PrefsKeys.CAL_GOAL] ?: profile?.calories ?: 2200
        eaten = prefs[PrefsKeys.CAL_EATEN] ?: 0
        proteinEaten = prefs[PrefsKeys.PROTEIN_EATEN] ?: 0
        fatEaten = prefs[PrefsKeys.FAT_EATEN] ?: 0
        carbsEaten = prefs[PrefsKeys.CARBS_EATEN] ?: 0
        achievementShown = prefs[PrefsKeys.ACH_GOAL_REACHED] ?: false

        val totalCompletions = prefs[PrefsKeys.ACH_TOTAL_GOAL_COMPLETIONS] ?: 0
        shownGoalCompletionMilestone = when {
            totalCompletions >= 7 -> 7
            totalCompletions >= 3 -> 3
            totalCompletions >= 1 -> 1
            else -> null
        }

        val saved = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
        entries.clear()
        entries.addAll(decodeEntries(saved))
    }

    val progress =
        if (goal > 0) (eaten.toFloat() / goal).coerceIn(0f, 1f) else 0f

    LaunchedEffect(eaten, goal) {
        if (eaten >= goal && !achievementShown) {
            achievementShown = true

            var unlockedMilestone: Int? = null

            context.prefsDataStore().edit { prefs ->
                val alreadyReachedToday = prefs[PrefsKeys.ACH_GOAL_REACHED] ?: false

                if (!alreadyReachedToday) {
                    val currentCount = prefs[PrefsKeys.ACH_TOTAL_GOAL_COMPLETIONS] ?: 0
                    val newCount = currentCount + 1

                    prefs[PrefsKeys.ACH_TOTAL_GOAL_COMPLETIONS] = newCount
                    prefs[PrefsKeys.ACH_GOAL_REACHED] = true

                    unlockedMilestone = when {
                        newCount >= 7 && shownGoalCompletionMilestone != 7 -> 7
                        newCount >= 3 && shownGoalCompletionMilestone != 3 -> 3
                        newCount >= 1 && shownGoalCompletionMilestone != 1 -> 1
                        else -> null
                    }
                }
            }

            showAchievement = true
            unlockedMilestone?.let {
                shownGoalCompletionMilestone = it
            }

            delay(2000)
            showAchievement = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

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
                    interactionSource = remember { MutableInteractionSource() },
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
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
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset((-75).dp * fabProgress, (45).dp * fabProgress)
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
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset(0.dp, (45).dp * fabProgress)
                            .scale(0.7f + 0.3f * fabProgress)
                            .alpha(fabProgress)
                    ) { Text("🎯") }
                }

                if (fabProgress > 0f) {
                    FloatingActionButton(
                        onClick = {
                            isFabOpen = false
                            showResetDialog = true
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset(75.dp * fabProgress, (45).dp * fabProgress)
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

            Spacer(modifier = Modifier.height(20.dp))

            NutritionProgressBlock(
                proteins = proteinEaten,
                fats = fatEaten,
                carbs = carbsEaten,
                proteinGoal = profile?.proteins ?: 0,
                fatGoal = profile?.fats ?: 0,
                carbsGoal = profile?.carbs ?: 0
            )
        }

        if (showAddDialog) {
            AddNutritionDialog(
                title = "Добавить калории",
                quickValues = listOf(50, 200, 550),
                onDismiss = { showAddDialog = false },
                onConfirm = { calories, proteins, fats, carbs ->
                    eaten += calories
                    proteinEaten += proteins
                    fatEaten += fats
                    carbsEaten += carbs

                    entries.add(
                        0,
                        CalorieEntry(
                            calories,
                            LocalTime.now().format(timeFormatter)
                        )
                    )

                    scope.launch {
                        val today = LocalDate.now().toString()

                        context.prefsDataStore().edit { prefs ->
                            prefs[PrefsKeys.CAL_EATEN] = eaten
                            prefs[PrefsKeys.PROTEIN_EATEN] = proteinEaten
                            prefs[PrefsKeys.FAT_EATEN] = fatEaten
                            prefs[PrefsKeys.CARBS_EATEN] = carbsEaten
                            prefs[PrefsKeys.CAL_ENTRIES] = encodeEntries(entries)

                            val history = decodeDailyProgressList(
                                prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
                            ).toMutableList()

                            val updatedItem = DailyProgress(
                                date = today,
                                eatenCalories = eaten,
                                goalCalories = goal,
                                goalReached = eaten >= goal
                            )

                            val existingIndex = history.indexOfFirst { it.date == today }
                            if (existingIndex >= 0) {
                                history[existingIndex] = updatedItem
                            } else {
                                history.add(updatedItem)
                            }

                            prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] =
                                encodeDailyProgressList(history.sortedBy { it.date })
                        }
                    }
                }
            )
        }

        if (showGoalDialog) {
            GoalInputDialog(
                title = "Новая цель",
                quickValues = listOf(1700, 2200, 2800),
                initialCalories = goal,
                initialProteins = profile?.proteins ?: 0,
                initialFats = profile?.fats ?: 0,
                initialCarbs = profile?.carbs ?: 0,
                onDismiss = { showGoalDialog = false },
                onConfirm = { caloriesGoal, proteinsGoal, fatsGoal, carbsGoal ->
                    goal = caloriesGoal
                    achievementShown = false

                    scope.launch {
                        val today = LocalDate.now().toString()

                        context.prefsDataStore().edit { prefs ->
                            prefs[PrefsKeys.CAL_GOAL] = goal

                            val history = decodeDailyProgressList(
                                prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
                            ).toMutableList()

                            val updatedItem = DailyProgress(
                                date = today,
                                eatenCalories = eaten,
                                goalCalories = goal,
                                goalReached = eaten >= goal
                            )

                            val existingIndex = history.indexOfFirst { it.date == today }
                            if (existingIndex >= 0) {
                                history[existingIndex] = updatedItem
                            } else {
                                history.add(updatedItem)
                            }

                            prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] =
                                encodeDailyProgressList(history.sortedBy { it.date })
                        }

                        profile?.let { currentProfile ->
                            context.saveUserProfile(
                                currentProfile.copy(
                                    calories = caloriesGoal,
                                    proteins = proteinsGoal,
                                    fats = fatsGoal,
                                    carbs = carbsGoal
                                )
                            )
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

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сброс прогресса") },
            text = {
                Text("Вы действительно хотите сбросить все добавленные калории и прогресс за день?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        eaten = 0
                        proteinEaten = 0
                        fatEaten = 0
                        carbsEaten = 0
                        entries.clear()

                        scope.launch {
                            val today = LocalDate.now().toString()

                            context.prefsDataStore().edit { prefs ->
                                prefs[PrefsKeys.CAL_EATEN] = 0
                                prefs[PrefsKeys.PROTEIN_EATEN] = 0
                                prefs[PrefsKeys.FAT_EATEN] = 0
                                prefs[PrefsKeys.CARBS_EATEN] = 0
                                prefs[PrefsKeys.CAL_ENTRIES] = ""

                                val history = decodeDailyProgressList(
                                    prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
                                ).toMutableList()

                                val updatedItem = DailyProgress(
                                    date = today,
                                    eatenCalories = 0,
                                    goalCalories = goal,
                                    goalReached = false
                                )

                                val existingIndex = history.indexOfFirst { it.date == today }
                                if (existingIndex >= 0) {
                                    history[existingIndex] = updatedItem
                                } else {
                                    history.add(updatedItem)
                                }

                                prefs[PrefsKeys.DAILY_PROGRESS_HISTORY] =
                                    encodeDailyProgressList(history.sortedBy { it.date })
                            }
                        }
                    }
                ) {
                    Text("Сбросить", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun AddNutritionDialog(
    title: String,
    quickValues: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (calories: Int, proteins: Int, fats: Int, carbs: Int) -> Unit
) {
    var calories by remember { mutableStateOf("") }
    var proteins by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    fun parseInt(value: String): Int = value.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Калории") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    quickValues.forEach { quickValue ->
                        Button(
                            onClick = {
                                onConfirm(
                                    quickValue,
                                    parseInt(proteins),
                                    parseInt(fats),
                                    parseInt(carbs)
                                )
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(quickValue.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val addInteractionSource = remember { MutableInteractionSource() }

                Text(
                    text = if (expanded) "Скрыть дополнительно" else "Дополнительно",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        interactionSource = addInteractionSource,
                        indication = null,
                        onClick = { expanded = !expanded }
                    )
                )

                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = proteins,
                        onValueChange = { proteins = it },
                        label = { Text("Белки") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it },
                        label = { Text("Жиры") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Углеводы") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val caloriesValue = calories.toIntOrNull() ?: return@TextButton
                    onConfirm(
                        caloriesValue,
                        parseInt(proteins),
                        parseInt(fats),
                        parseInt(carbs)
                    )
                    onDismiss()
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

@Composable
fun GoalInputDialog(
    title: String,
    quickValues: List<Int>,
    initialCalories: Int,
    initialProteins: Int,
    initialFats: Int,
    initialCarbs: Int,
    onDismiss: () -> Unit,
    onConfirm: (caloriesGoal: Int, proteinsGoal: Int, fatsGoal: Int, carbsGoal: Int) -> Unit
) {
    var calories by remember { mutableStateOf(initialCalories.toString()) }
    var proteins by remember { mutableStateOf(initialProteins.toString()) }
    var fats by remember { mutableStateOf(initialFats.toString()) }
    var carbs by remember { mutableStateOf(initialCarbs.toString()) }
    var expanded by remember { mutableStateOf(false) }

    fun parseGoal(value: String, fallback: Int): Int = value.toIntOrNull() ?: fallback

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Цель по калориям") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Быстрый выбор:",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    quickValues.forEach { quickValue ->
                        Button(
                            onClick = {
                                onConfirm(
                                    quickValue,
                                    parseGoal(proteins, initialProteins),
                                    parseGoal(fats, initialFats),
                                    parseGoal(carbs, initialCarbs)
                                )
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(quickValue.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val goalInteractionSource = remember { MutableInteractionSource() }

                Text(
                    text = if (expanded) "Скрыть дополнительно" else "Дополнительно",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(
                        interactionSource = goalInteractionSource,
                        indication = null,
                        onClick = { expanded = !expanded }
                    )
                )

                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = proteins,
                        onValueChange = { proteins = it },
                        label = { Text("Цель по белкам") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fats,
                        onValueChange = { fats = it },
                        label = { Text("Цель по жирам") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Цель по углеводам") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val caloriesGoal = calories.toIntOrNull() ?: return@TextButton
                    onConfirm(
                        caloriesGoal,
                        parseGoal(proteins, initialProteins),
                        parseGoal(fats, initialFats),
                        parseGoal(carbs, initialCarbs)
                    )
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun NutritionProgressBlock(
    proteins: Int,
    fats: Int,
    carbs: Int,
    proteinGoal: Int,
    fatGoal: Int,
    carbsGoal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Прогресс по БЖУ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            NutritionProgressRow(
                title = "Белки",
                value = proteins,
                goal = proteinGoal,
                gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF1E88E5))
            )

            NutritionProgressRow(
                title = "Жиры",
                value = fats,
                goal = fatGoal,
                gradientColors = listOf(Color(0xFFFFA726), Color(0xFFFB8C00))
            )

            NutritionProgressRow(
                title = "Углеводы",
                value = carbs,
                goal = carbsGoal,
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
            )
        }
    }
}

@Composable
private fun NutritionProgressRow(
    title: String,
    value: Int,
    goal: Int,
    gradientColors: List<Color>
) {
    val progress = if (goal > 0) {
        (value.toFloat() / goal).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$value / $goal г",
                color = Color.Gray
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(
                    color = Color(0xFFE8E8E8),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp)
                    .background(
                        brush = Brush.horizontalGradient(gradientColors),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}