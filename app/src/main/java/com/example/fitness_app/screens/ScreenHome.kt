package com.example.fitness_app.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.fitness_app.nutrition.NutritionCalculator
import com.example.fitness_app.DATA.CalorieEntry
import com.example.fitness_app.DATA.encodeEntries
import com.example.fitness_app.DATA.decodeEntries





// Кастомный круговой ProgressBar для калорий
@Composable
fun CircularProgressBar(
    percentage: Float, // процент выполнения цели (0f..1f)
    number: Int, // целевое значение калорий
    fontSize: TextUnit = 28.sp,
    radius: Dp = 60.dp,
    color: Color = Color.Green,
    strokeWidth: Dp = 8.dp,
    animDuration: Int = 1000 // длительность анимации
) {
    // Флаг, чтобы анимация запускалась только один раз
    var animationPlayed by remember { mutableStateOf(false) }

    // Анимация заполнения круга
    val curPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(animDuration),
        label = "progress_animation"
    )

    // Запуск анимации при первом отображении
    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    // Контейнер с Canvas и текстом внутри
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

        // Текущие калории (прогресс * цель)
        Text(
            text = (curPercentage * number).toInt().toString(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}


// Главный экран приложения
@Composable
fun ScreenHome() {
    // Контекст и coroutineScope для работы с DataStore
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ВРЕМЕННЫЕ значения профиля пользователя (заглушки для MVP)
    // В будущем будут браться из экрана заполнения профиля
    val gender = Gender.Мужской
    val age = AgeGroup.A21_35
    val heightGroup = HeightGroup.H171_180
    val weightGroup = WeightGroup.W71_85
    val activity = ActivityLevel.MEDIUM
    val goalType = Goal.MAINTAIN

    // Состояние для подтверждения сброса
    var showResetDialog by remember { mutableStateOf(false) }

    // Расчёт дневной нормы питания
    val nutrition = remember {
        NutritionCalculator.calculate(
            gender = gender,
            age = age,
            height = heightGroup,
            weight = weightGroup,
            activity = activity,
            goal = goalType
        )
    }

    // Формат времени для записей калорий
    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    // Состояния цели и съеденных калорий
    var goal by remember { mutableStateOf(2200) }
    var eaten by remember { mutableStateOf(0) }

    // Флаги для анимации достижения цели
    var showAchievement by remember { mutableStateOf(false) }
    var achievementShown by remember { mutableStateOf(false) }

    // Управление диалогами
    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    // Состояние раскрытия FAB-меню
    var isFabOpen by remember { mutableStateOf(false) }

    // Анимация FAB
    val fabProgress by animateFloatAsState(
        targetValue = if (isFabOpen) 1f else 0f,
        animationSpec = tween(400),
        label = "fab_anim"
    )

    // Список добавленных калорий
    val entries = remember { mutableStateListOf<CalorieEntry>() }

    // Загрузка сохранённых данных из DataStore
    LaunchedEffect(Unit) {
        val prefs = context.prefsDataStore().data.first()

        goal = prefs[PrefsKeys.CAL_GOAL] ?: 2200
        eaten = prefs[PrefsKeys.CAL_EATEN] ?: 0

        val saved = prefs[PrefsKeys.CAL_ENTRIES] ?: ""
        entries.clear()
        entries.addAll(decodeEntries(saved))
    }

    // Расчёт процента выполнения цели
    val progress =
        if (goal > 0) (eaten.toFloat() / goal).coerceIn(0f, 1f) else 0f


    // Проверка достижения цели
    LaunchedEffect(eaten, goal) {
        if (eaten >= goal && !achievementShown) {
            showAchievement = true
            achievementShown = true

            delay(2000)
            showAchievement = false

            // Сохраняем факт достижения цели
            context.prefsDataStore().edit {
                it[PrefsKeys.ACH_GOAL_REACHED] = true
            }
        }
    }


// UI
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            // Блок с прогрессбаром и FAB
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

                // Главная FAB-кнопка
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

                // FAB — добавить калории
                if (fabProgress > 0f) {
                    FloatingActionButton(
                        onClick = {
                            isFabOpen = false
                            showAddDialog = true
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset((-70).dp * fabProgress, (-10).dp * fabProgress)
                            .scale(0.7f + 0.3f * fabProgress)
                            .alpha(fabProgress)
                    ) { Text("+") }
                }

                // FAB — изменить цель
                if (fabProgress > 0f) {
                    FloatingActionButton(
                        onClick = {
                            isFabOpen = false
                            showGoalDialog = true
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset(0.dp, (-80).dp * fabProgress)
                            .scale(0.7f + 0.3f * fabProgress)
                            .alpha(fabProgress)
                    ) { Text("🎯") }
                }

                // FAB — сброс данных
                if (fabProgress > 0f) {
                    FloatingActionButton(
                        onClick = {
                            isFabOpen = false
                            showResetDialog = true
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        elevation = FloatingActionButtonDefaults.elevation(0.dp),
                        modifier = Modifier
                            .offset(70.dp * fabProgress, (-10).dp * fabProgress)
                            .scale(0.7f + 0.3f * fabProgress)
                            .alpha(fabProgress),
                        containerColor = Color.Red
                    ) { Text("⟳") }
                }
            }


            Spacer(modifier = Modifier.height(52.dp))

            // Список добавленных калорий
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
                                    modifier = Modifier
                                        .fillMaxWidth(),
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


            Spacer(modifier = Modifier.height(24.dp))

            // Блок с дневной нормой (пока заглушка)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ваша дневная норма",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Калории: ${nutrition.calories} ккал")
                    Text("Белки: ${nutrition.proteins} г")
                    Text("Жиры: ${nutrition.fats} г")
                    Text("Углеводы: ${nutrition.carbs} г")
                }
            }


        }


        // Диалог добавления калорий
        if (showAddDialog) {
            SimpleInputDialog(
                title = "Добавить калории",
                quickValues = listOf(50, 200, 550), // ★ ИЗМЕНЕНО
                onDismiss = { showAddDialog = false },
                onConfirm = { value ->
                    eaten += value

                    entries.add(
                        0,
                        CalorieEntry(
                            value,
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

        // Диалог изменения цели
        if (showGoalDialog) {
            SimpleInputDialog(
                title = "Новая цель",
                quickValues = listOf(1700, 2200, 2800), // ★ ИЗМЕНЕНО
                onDismiss = { showGoalDialog = false },
                onConfirm = { value ->
                    goal = value
                    achievementShown = false

                    scope.launch {
                        context.prefsDataStore().edit {
                            it[PrefsKeys.CAL_GOAL] = goal
                        }
                    }
                }
            )
        }

        // Анимация достижения цели
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

    //Спрашиваем пользователя хочет ли реально сбросить прогресс бар
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
                        achievementShown = false
                        entries.clear()

                        scope.launch {
                            context.prefsDataStore().edit {
                                it[PrefsKeys.CAL_EATEN] = 0
                                it[PrefsKeys.ACH_GOAL_REACHED] = false
                                it[PrefsKeys.CAL_ENTRIES] = ""
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


// Диалог ввода калорий
@Composable
fun SimpleInputDialog(
    title: String,
    quickValues: List<Int>,
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
                    quickValues.forEach { value ->
                        Button(
                            onClick = {
                                onConfirm(value)
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(value.toString())
                        }
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
