package com.example.fitness_app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitness_app.core.datastore.clearUserProfile
import com.example.fitness_app.core.datastore.dailyProgressHistoryFlow
import com.example.fitness_app.core.datastore.userProfileFlow
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    onProfileDeleted: () -> Unit
) {
    val context = LocalContext.current
    val profile by context.userProfileFlow().collectAsState(initial = null)
    val history by context.dailyProgressHistoryFlow().collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (profile == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Профиль не найден")
        }
        return
    }

    val user = profile!!
    val currentMonth = YearMonth.now()

    val completedDaysInCurrentMonth = history
        .filter { item ->
            runCatching {
                val date = LocalDate.parse(item.date)
                date.year == currentMonth.year &&
                        date.month == currentMonth.month &&
                        item.goalReached
            }.getOrDefault(false)
        }
        .map { LocalDate.parse(it.date).dayOfMonth }
        .toSet()

    val usedDaysCount = history.size
    val successfulDaysCount = history.count { it.goalReached }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить профиль?") },
            text = { Text("Все данные профиля будут удалены с устройства.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        CoroutineScope(Dispatchers.Main).launch {
                            context.clearUserProfile()
                            onProfileDeleted()
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileHeaderCard(
            goalLabel = goalLabel(user.goal),
            calories = user.calories
        )

        SectionCard(title = "Параметры пользователя") {
            ProfileInfoRow("Пол", genderLabel(user.gender))
            HorizontalDivider()
            ProfileInfoRow("Возраст", user.age.label)
            HorizontalDivider()
            ProfileInfoRow("Рост", user.height.label)
            HorizontalDivider()
            ProfileInfoRow("Вес", user.weight.label)
            HorizontalDivider()
            ProfileInfoRow("Активность", user.activity.label)
            HorizontalDivider()
            ProfileInfoRow("Цель", user.goal.label)
        }

        SectionCard(title = "Дневные нормы") {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2
            ) {
                NutritionTile(
                    title = "Калории",
                    value = "${user.calories} ккал",
                    modifier = Modifier.weight(1f, fill = true)
                )
                NutritionTile(
                    title = "Белки",
                    value = "${user.proteins} г",
                    modifier = Modifier.weight(1f, fill = true)
                )
                NutritionTile(
                    title = "Жиры",
                    value = "${user.fats} г",
                    modifier = Modifier.weight(1f, fill = true)
                )
                NutritionTile(
                    title = "Углеводы",
                    value = "${user.carbs} г",
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
        }

        SectionCard(title = "Статистика") {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 2
            ) {
                StatTile(
                    title = "Дней использования",
                    value = usedDaysCount.toString(),
                    modifier = Modifier.weight(1f, fill = true)
                )
                StatTile(
                    title = "Успешных дней",
                    value = successfulDaysCount.toString(),
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
        }

        SectionCard(title = "Календарь выполнения цели") {
            Text(
                text = "Подсвечены дни текущего месяца, в которые цель по калориям была выполнена.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            CalendarLegend()

            Spacer(modifier = Modifier.height(8.dp))

            GoalCalendar(completedDays = completedDaysInCurrentMonth)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Редактировать")
            }

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Удалить")
            }
        }
        Spacer(modifier = Modifier.height(34.dp))

    }
}

@Composable
private fun ProfileHeaderCard(
    goalLabel: String,
    calories: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Профиль пользователя",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Ваши персональные данные и текущие дневные нормы",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Цель: $goalLabel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Дневная норма: $calories ккал",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun NutritionTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LegendItem(
            color = MaterialTheme.colorScheme.primary,
            text = "Цель выполнена"
        )
        LegendItem(
            color = MaterialTheme.colorScheme.secondaryContainer,
            text = "Сегодня"
        )
    }
}

@Composable
private fun LegendItem(
    color: androidx.compose.ui.graphics.Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = color, shape = CircleShape)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun GoalCalendar(
    completedDays: Set<Int>,
    month: YearMonth = YearMonth.now()
) {
    val today = LocalDate.now().dayOfMonth
    val daysInMonth = month.lengthOfMonth()
    val firstDayOffset = month.atDay(1).dayOfWeek.value % 7

    val dayLabels = listOf("Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
    val monthTitle = month.atDay(1)
        .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))
        .replaceFirstChar { it.titlecase(Locale("ru")) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = monthTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        var currentDay = 1
        repeat(6) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(7) { columnIndex ->
                    val cellIndex = rowIndex * 7 + columnIndex
                    val isEmpty = cellIndex < firstDayOffset || currentDay > daysInMonth

                    if (isEmpty) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        val day = currentDay
                        val isCompleted = completedDays.contains(day)
                        val isToday = day == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    color = when {
                                        isCompleted -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = when {
                                    isCompleted -> MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isToday || isCompleted) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                }
                            )
                        }
                        currentDay++
                    }
                }
            }

            if (currentDay > daysInMonth) return@repeat
        }
    }
}

private fun genderLabel(gender: Gender): String {
    return when (gender) {
        Gender.MALE -> "Мужской"
        Gender.FEMALE -> "Женский"
    }
}

private fun goalLabel(goal: Goal): String = goal.label