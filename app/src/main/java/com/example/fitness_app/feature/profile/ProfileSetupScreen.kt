package com.example.fitness_app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness_app.core.datastore.UserProfileData
import com.example.fitness_app.core.datastore.saveUserProfile
import com.example.fitness_app.core.datastore.userProfileFlow
import com.example.fitness_app.domain.model.ActivityLevel
import com.example.fitness_app.domain.model.AgeGroup
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import com.example.fitness_app.domain.model.HeightGroup
import com.example.fitness_app.domain.model.WeightGroup
import com.example.fitness_app.domain.nutrition.NutritionCalculator
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember

@Composable
fun ProfileSetupScreen(
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val existingProfile by context.userProfileFlow().collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var step by remember { mutableIntStateOf(0) }

    var gender by remember(existingProfile) { mutableStateOf(existingProfile?.gender) }
    var age by remember(existingProfile) { mutableStateOf(existingProfile?.age) }
    var height by remember(existingProfile) { mutableStateOf(existingProfile?.height) }
    var weight by remember(existingProfile) { mutableStateOf(existingProfile?.weight) }
    var activity by remember(existingProfile) { mutableStateOf(existingProfile?.activity) }
    var goal by remember(existingProfile) { mutableStateOf(existingProfile?.goal) }

    var isSaving by remember { mutableStateOf(false) }

    val canGoNext = when (step) {
        0 -> gender != null
        1 -> age != null
        2 -> height != null
        3 -> weight != null
        4 -> activity != null
        5 -> goal != null
        else -> true
    }

    val calculationResult = if (
        gender != null &&
        age != null &&
        height != null &&
        weight != null &&
        activity != null &&
        goal != null
    ) {
        NutritionCalculator.calculate(
            gender = gender!!,
            age = age!!,
            height = height!!,
            weight = weight!!,
            activity = activity!!,
            goal = goal!!
        )
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 44.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (existingProfile == null) "Анкета профиля" else "Редактирование профиля",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            when (step) {
                0 -> SelectionStep(
                    title = "Ваш пол",
                    options = Gender.values().toList(),
                    label = {
                        when (it) {
                            Gender.MALE -> "Мужской"
                            Gender.FEMALE -> "Женский"
                        }
                    },
                    selected = gender,
                    onSelect = { gender = it }
                )

                1 -> SelectionStep(
                    title = "Возраст",
                    options = AgeGroup.values().toList(),
                    label = { it.label },
                    selected = age,
                    onSelect = { age = it }
                )

                2 -> SelectionStep(
                    title = "Рост",
                    options = HeightGroup.values().toList(),
                    label = { it.label },
                    selected = height,
                    onSelect = { height = it }
                )

                3 -> SelectionStep(
                    title = "Вес",
                    options = WeightGroup.values().toList(),
                    label = { it.label },
                    selected = weight,
                    onSelect = { weight = it }
                )

                4 -> SelectionStep(
                    title = "Уровень активности",
                    options = ActivityLevel.values().toList(),
                    label = { it.label },
                    selected = activity,
                    onSelect = { activity = it }
                )

                5 -> SelectionStep(
                    title = "Цель",
                    options = Goal.values().toList(),
                    label = { it.label },
                    selected = goal,
                    onSelect = { goal = it }
                )

                6 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Проверьте данные",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SummaryRow("Пол", genderLabel(gender))
                        SummaryRow("Возраст", age?.label ?: "-")
                        SummaryRow("Рост", height?.label ?: "-")
                        SummaryRow("Вес", weight?.label ?: "-")
                        SummaryRow("Активность", activity?.label ?: "-")
                        SummaryRow("Цель", goal?.label ?: "-")

                        Spacer(modifier = Modifier.height(20.dp))

                        if (calculationResult != null) {
                            Text(
                                text = "Ваши рассчитанные нормы",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            SummaryRow("Калории", "${calculationResult.calories} ккал")
                            SummaryRow("Белки", "${calculationResult.proteins} г")
                            SummaryRow("Жиры", "${calculationResult.fats} г")
                            SummaryRow("Углеводы", "${calculationResult.carbs} г")
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    TextButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Назад",
                            fontSize = 18.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < 6) {
                            step++
                        } else {
                            val result = calculationResult ?: return@Button

                            val profile = UserProfileData(
                                gender = gender!!,
                                age = age!!,
                                height = height!!,
                                weight = weight!!,
                                activity = activity!!,
                                goal = goal!!,
                                calories = result.calories,
                                proteins = result.proteins,
                                fats = result.fats,
                                carbs = result.carbs
                            )

                            scope.launch {
                                isSaving = true
                                context.saveUserProfile(profile)
                                isSaving = false
                                onSaved()
                            }
                        }
                    },
                    enabled = canGoNext && !isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = if (step < 6) "Далее" else "Сохранить",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun <T> SelectionStep(
    title: String,
    options: List<T>,
    label: (T) -> String,
    selected: T?,
    onSelect: (T) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            options.forEach { item ->
                SelectionOptionCard(
                    text = label(item),
                    selected = selected == item,
                    onClick = { onSelect(item) }
                )
            }
        }
    }
}

@Composable
private fun SelectionOptionCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    }

    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionIndicator(selected = selected)

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean) {
    val outerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    }

    Box(
        modifier = Modifier
            .size(22.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 2.dp,
                color = outerColor,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 17.sp
            )
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun genderLabel(gender: Gender?): String {
    return when (gender) {
        Gender.MALE -> "Мужской"
        Gender.FEMALE -> "Женский"
        null -> "-"
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreen(onSaved = {})
}