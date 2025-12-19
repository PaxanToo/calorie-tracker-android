package com.example.fitness_app.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



fun getBotResponse(message: String): String {
    return when {
        message.contains("привет", ignoreCase = true) ->
            "Привет! Рад тебя видеть 😊"

        message.contains("помощь", ignoreCase = true) ->
            "Я могу помочь с подсчётом калорий и мотивацией "

        message.contains("калори", ignoreCase = true) ->
            "Средняя дневная норма — около 2000–2500 ккал"

        else ->
            "Я пока учусь отвечать, попробуй написать «помощь»"
    }
}



@Composable
fun ScreenChat() {
    val messages = remember { mutableStateListOf<Message>() }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 180.dp)
    ) {

        // Список сообщений
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                Text(
                    text = if (message.isUser) "Вы: ${message.text}" else "Бот: ${message.text}"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                label = { Text("Введите сообщение") }
            )

            Button(
                onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        messages.add(Message(text = text, isUser = true))
                        inputText = ""

                        val botReply = getBotResponse(text)
                        messages.add(
                            Message(
                                text = botReply,
                                isUser = false
                            ) )
                    }
                }
            ) {
                Text("Отправить")
            }
        }
    }
}