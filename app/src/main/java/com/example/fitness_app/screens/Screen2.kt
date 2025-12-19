package com.example.fitness_app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.ItemDb
import com.example.fitness_app.MainViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha



@Preview(showBackground = true)
@Composable
fun Screen2Preview() {
    Screen222()
}

@Composable
fun Screen222(
    viewModel: MainViewModel = viewModel()
) {


    val height = remember { mutableStateOf("") }
    val weight = remember { mutableStateOf("") }
    val result = remember { mutableStateOf("") }


    val items = viewModel.items.collectAsState().value


    val isMenuExtend = remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (isMenuExtend.value) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "fab_progr"

    )



    Box(modifier = Modifier.fillMaxSize()) {


        FloatingActionButton(
            onClick = {
                isMenuExtend.value = !isMenuExtend.value
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp)
                .rotate(225f * animationProgress)
                .scale(1f + 0.15f * animationProgress)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Toggle menu"
            )
        }


        if (animationProgress > 0f) {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp)
                    .offset(x = (-80).dp * animationProgress)
                    .scale(0.6f + 0.4f * animationProgress)
                    .alpha(animationProgress)
            ) {
                Text("1")
            }
        }



        if (animationProgress > 0f) {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp)
                    .offset(
                        y = (-90).dp * animationProgress
                    )
                    .scale(0.6f + 0.4f * animationProgress)
                    .alpha(animationProgress)
            ) {
                Text("2")
            }
        }


        if (animationProgress > 0f) {
            FloatingActionButton(
                onClick = {
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 150.dp)
                    .offset(x = (80).dp * animationProgress)
                    .scale(0.6f + 0.4f * animationProgress)
                    .alpha(animationProgress)
            ) {
                Text("3")
            }
        }










        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center
        ) {

            /*
        Box(

            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()

        )
        {
            Button(onClick = {








            }) { }

         */







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
                val heightCm = height.value.toIntOrNull()
                val weightKg = weight.value.toIntOrNull()

                if (heightCm != null && weightKg != null && heightCm > 0) {
                    val heightM = heightCm / 100f
                    val bmi = weightKg / (heightM * heightM)


                    viewModel.insertItem(
                        ItemDb(
                            name = "Запись",
                            weight = weightKg,
                            height = heightCm.toString()
                        )
                    )


                    result.value = "ИМТ: ${"%.2f".format(bmi)}"
                } else {
                    result.value = "Введите корректные данные"
                }


            }) {
                Text("Сохранить")
            }
            Spacer(modifier = Modifier.height(24.dp))


            Text("Сохранённые записи:")


            items.forEach {
                Text("Рост: ${it.height} Вес: ${it.weight}")
            }


        }


    }
}








