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



@Composable
fun ProfileSetupScreen() {

    var step by remember { mutableStateOf(0) }

    var gender by remember { mutableStateOf<Gender?>(null) }
    var age by remember { mutableStateOf<AgeGroup?>(null) }
    var height by remember { mutableStateOf<HeightGroup?>(null) }
    var weight by remember { mutableStateOf<WeightGroup?>(null) }
    var activity by remember { mutableStateOf<ActivityLevel?>(null) }
    var goal by remember { mutableStateOf<Goal?>(null) }

    val canGoNext = when (step) {
        0 -> gender != null
        1 -> age != null
        2 -> height != null
        3 -> weight != null
        4 -> activity != null
        5 -> goal != null
        else -> true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp,
                end = 24.dp,
                top = 48.dp,
                bottom = 80.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

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
                Column {
                    Text("Готово 🎉", fontSize = 22.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Пол: ${
                            when (gender) {
                                Gender.MALE -> "Мужской"
                                Gender.FEMALE -> "Женский"
                                null -> "-"
                            }
                        }"
                    )
                    Text("Возраст: ${age?.label}")
                    Text("Рост: ${height?.label}")
                    Text("Вес: ${weight?.label}")
                    Text("Активность: ${activity?.label}")
                    Text("Цель: ${goal?.label}")
                }
            }
        }

        Button(
            onClick = {
                if (step < 6) {
                    step++
                } else {
                    //потом тут навигаци и сохранение в будущем
                }
            },
            enabled = canGoNext,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
        ) {
            Text(if (step < 6) "Далее" else "Сохранить")
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



@Preview(showBackground = true)
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreen()
}
