package com.example.fitness_app.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness_app.domain.model.ActivityLevel
import com.example.fitness_app.domain.model.AgeGroup
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import com.example.fitness_app.domain.model.HeightGroup
import com.example.fitness_app.domain.model.WeightGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import com.example.fitness_app.core.datastore.UserProfileData
import com.example.fitness_app.core.datastore.saveUserProfile
import com.example.fitness_app.core.datastore.userProfileFlow
import com.example.fitness_app.domain.nutrition.NutritionCalculator
import kotlinx.coroutines.launch



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
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (existingProfile == null) "Анкета профиля" else "Редактирование профиля",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Проверьте данные", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Пол: ${genderLabel(gender)}")
                        Text("Возраст: ${age?.label ?: "-"}")
                        Text("Рост: ${height?.label ?: "-"}")
                        Text("Вес: ${weight?.label ?: "-"}")
                        Text("Активность: ${activity?.label ?: "-"}")
                        Text("Цель: ${goal?.label ?: "-"}")

                        Spacer(modifier = Modifier.height(16.dp))

                        if (calculationResult != null) {
                            Text("Норма калорий: ${calculationResult.calories} ккал")
                            Text("Белки: ${calculationResult.proteins} г")
                            Text("Жиры: ${calculationResult.fats} г")
                            Text("Углеводы: ${calculationResult.carbs} г")
                        }
                    }
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    TextButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Назад")
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
                        Text(if (step < 6) "Далее" else "Сохранить")
                    }
                }
            }
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
        Text(title, fontSize = 22.sp)
        Spacer(Modifier.height(16.dp))

        options.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == item,
                    onClick = { onSelect(item) }
                )
                Spacer(Modifier.width(8.dp))
                Text(label(item))
            }
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
