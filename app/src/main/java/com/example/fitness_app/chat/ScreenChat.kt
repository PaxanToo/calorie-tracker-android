package com.example.fitness_app.chat

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType



fun getBotResponse(message: String): String {
    return when {
        message.contains("привет", ignoreCase = true) ->
            "Привет! Рад тебя видеть "

        message.contains("помощь", ignoreCase = true) ->
            "Я могу помочь с подсчётом калорий и мотивацией "

        message.contains("калории", ignoreCase = true) ->
            "Средняя дневная норма — около 2000–2500 ккал"

        else ->
            "Я пока учусь отвечать, попробуй написать «помощь»"
    }
}




@Composable
fun ChatBubble(message: Message) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(4.dp)
                .background(
                    color = if (isUser) {
                        androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    } else {
                        androidx.compose.ui.graphics.Color(0xFFE0E0E0)
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) {
                    androidx.compose.ui.graphics.Color.White
                } else {
                    androidx.compose.ui.graphics.Color.Black
                }
            )
        }
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
                top = 85.dp,
                bottom = 180.dp)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
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
                label = { Text("Введите сообщение") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
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