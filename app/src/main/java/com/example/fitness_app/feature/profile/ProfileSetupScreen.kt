package com.example.fitness_app.feature.profile

import androidx.compose.foundation.BorderStroke
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
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import com.example.fitness_app.domain.nutrition.NutritionCalculator
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.fitness_app.core.utils.vibrateShort

@Composable
fun ProfileSetupScreen(
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val existingProfile by context.userProfileFlow().collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var gender by remember(existingProfile) { mutableStateOf(existingProfile?.gender) }
    var ageText by remember(existingProfile) { mutableStateOf(existingProfile?.age?.toString() ?: "") }
    var heightText by remember(existingProfile) { mutableStateOf(existingProfile?.height?.toString() ?: "") }
    var weightText by remember(existingProfile) { mutableStateOf(existingProfile?.weight?.toString() ?: "") }
    var activity by remember(existingProfile) { mutableStateOf(existingProfile?.activity) }
    var goal by remember(existingProfile) { mutableStateOf(existingProfile?.goal) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val ageValue = ageText.toIntOrNull()
    val heightValue = heightText.toIntOrNull()
    val weightValue = weightText.toIntOrNull()

    val calculationResult = if (
        gender != null &&
        ageValue != null &&
        heightValue != null &&
        weightValue != null &&
        activity != null &&
        goal != null &&
        ageValue in 9..100 &&
        heightValue in 120..230 &&
        weightValue in 30..250
    ) {
        NutritionCalculator.calculate(
            gender = gender!!,
            age = ageValue,
            height = heightValue,
            weight = weightValue,
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
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = if (existingProfile == null) { "Анкета профиля" } else { "Редактирование профиля" },
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        SelectionStep(
            title = "Ваш пол",
            options = Gender.values().toList(),
            label = { genderLabel(it) },
            selected = gender,
            onSelect = {
                gender = it
                errorMessage = null
            }
        )

        InputStep(
            title = "Возраст",
            value = ageText,
            onValueChange = {
                ageText = it.filter { char -> char.isDigit() }
                errorMessage = null
            },
            placeholder = "Например: 22",
            suffix = "лет"
        )

        InputStep(
            title = "Рост",
            value = heightText,
            onValueChange = {
                heightText = it.filter { char -> char.isDigit() }
                errorMessage = null
            },
            placeholder = "Например: 178",
            suffix = "см"
        )

        InputStep(
            title = "Вес",
            value = weightText,
            onValueChange = {
                weightText = it.filter { char -> char.isDigit() }
                errorMessage = null
            },
            placeholder = "Например: 76",
            suffix = "кг"
        )

        SelectionStep(
            title = "Уровень активности",
            options = ActivityLevel.values().toList(),
            label = { it.label },
            selected = activity,
            onSelect = {
                activity = it
                errorMessage = null
            }
        )

        SelectionStep(
            title = "Цель",
            options = Goal.values().toList(),
            label = { it.label },
            selected = goal,
            onSelect = {
                goal = it
                errorMessage = null
            }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 15.sp
            )
        }

        if (calculationResult != null) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Ваши рассчитанные нормы",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                SummaryRow("Калории", "${calculationResult.calories} ккал")
                SummaryRow("Белки", "${calculationResult.proteins} г")
                SummaryRow("Жиры", "${calculationResult.fats} г")
                SummaryRow("Углеводы", "${calculationResult.carbs} г")
            }
        }

        Button(
            onClick = {
                val validatedAge = ageText.toIntOrNull()
                val validatedHeight = heightText.toIntOrNull()
                val validatedWeight = weightText.toIntOrNull()

                when {
                    gender == null -> {
                        errorMessage = "Выберите пол"
                        return@Button
                    }

                    validatedAge == null || validatedAge !in 9..100 -> {
                        errorMessage = "Введите корректный возраст от 9 до 100 лет"
                        return@Button
                    }

                    validatedHeight == null || validatedHeight !in 120..230 -> {
                        errorMessage = "Введите корректный рост от 120 до 230 см"
                        return@Button
                    }

                    validatedWeight == null || validatedWeight !in 30..250 -> {
                        errorMessage = "Введите корректный вес от 30 до 250 кг"
                        return@Button
                    }

                    activity == null -> {
                        errorMessage = "Выберите уровень активности"
                        return@Button
                    }

                    goal == null -> {
                        errorMessage = "Выберите цель"
                        return@Button
                    }
                }

                val finalAge = validatedAge
                val finalHeight = validatedHeight
                val finalWeight = validatedWeight

                if (finalAge == null || finalHeight == null || finalWeight == null) {
                    errorMessage = "Проверьте возраст, рост и вес"
                    return@Button
                }

                val result = NutritionCalculator.calculate(
                    gender = gender!!,
                    age = finalAge,
                    height = finalHeight,
                    weight = finalWeight,
                    activity = activity!!,
                    goal = goal!!
                )

                val profile = UserProfileData(
                    gender = gender!!,
                    age = finalAge,
                    height = finalHeight,
                    weight = finalWeight,
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
                    context.vibrateShort()
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSaving) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "Сохранить",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun InputStep(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String
) {
    Column {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholder)
            },
            suffix = {
                Text(suffix)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )
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
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

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
        border = BorderStroke(1.5.dp, borderColor),
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
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                }
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