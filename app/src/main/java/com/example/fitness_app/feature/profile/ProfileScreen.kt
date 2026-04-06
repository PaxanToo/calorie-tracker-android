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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.fitness_app.core.datastore.userProfileFlow
import com.example.fitness_app.domain.model.ActivityLevel
import com.example.fitness_app.domain.model.AgeGroup
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import com.example.fitness_app.domain.model.HeightGroup
import com.example.fitness_app.domain.model.WeightGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import com.example.fitness_app.core.datastore.dailyProgressHistoryFlow
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Профиль пользователя",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Цель: ${goalLabel(user.goal)}")
                Text("Дневная норма: ${user.calories} ккал")
            }
        }

        SectionCard(title = "Мои параметры") {
            ProfileInfoRow("Пол", genderLabel(user.gender))
            ProfileInfoRow("Возраст", user.age.label)
            ProfileInfoRow("Рост", user.height.label)
            ProfileInfoRow("Вес", user.weight.label)
            ProfileInfoRow("Активность", user.activity.label)
            ProfileInfoRow("Цель", user.goal.label)
        }

        SectionCard(title = "Мои нормы") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NutritionChip("Калории", "${user.calories} ккал")
                NutritionChip("Белки", "${user.proteins} г")
                NutritionChip("Жиры", "${user.fats} г")
                NutritionChip("Углеводы", "${user.carbs} г")
            }
        }

        SectionCard(title = "Статистика") {
            ProfileInfoRow("Дней использования", usedDaysCount.toString())
            ProfileInfoRow("Дней с выполненной целью", successfulDaysCount.toString())
        }

        SectionCard(title = "Календарь выполнения цели") {
            Text(
                text = "Подсвечены дни текущего месяца, в которые пользователь выполнил цель по калориям.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                Text("Удалить профиль")
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
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NutritionChip(
    title: String,
    value: String
) {
    Card(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
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

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val monthTitle = month.atDay(1)
            .format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru")))

        Text(
            text = monthTitle.replaceFirstChar { it.titlecase(Locale("ru")) },
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dayLabels.forEach { label ->
                Box(
                    modifier = Modifier.width(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        var currentDay = 1
        repeat(6) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(7) { columnIndex ->
                    val cellIndex = rowIndex * 7 + columnIndex
                    val isEmpty = cellIndex < firstDayOffset || currentDay > daysInMonth

                    if (isEmpty) {
                        Box(modifier = Modifier.size(32.dp))
                    } else {
                        val day = currentDay
                        val isCompleted = completedDays.contains(day)
                        val isToday = day == today

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = when {
                                        isCompleted -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = when {
                                    isCompleted -> MaterialTheme.colorScheme.onPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
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