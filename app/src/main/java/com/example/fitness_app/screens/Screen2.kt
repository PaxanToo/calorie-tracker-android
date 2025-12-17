package com.example.fitness_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Preview(showBackground = true)
@Composable
fun Screen2Preview() {
    Screen222()
}

@Composable
fun Screen222() {


    val height = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    val result = remember { mutableStateOf("") }













    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Введите данные", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = result.value, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))



        OutlinedTextField(
            value = height.value,
            onValueChange = { height.value = it },
            label = { Text("Рост (см)") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weight.value,
            onValueChange = { weight.value = it },
            label = { Text("Вес (кг)") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val heightCm = height.value.toFloatOrNull()
            val weightKg = weight.value.toFloatOrNull()

            if (heightCm != null && weightKg != null && heightCm > 0) {
                val heightM = heightCm / 100
                val bmi = weightKg / (heightM * heightM)

                result.value =
                    "Рост: ${height.value} см\n" +
                            "Вес: ${weight.value} кг\n" +
                            "ИМТ: ${"%.2f".format(bmi)}"
            } else {
                result.value = "Введите корректные значения"
            }
        }) {
            Text("Сохранить")
        }
    }
}




