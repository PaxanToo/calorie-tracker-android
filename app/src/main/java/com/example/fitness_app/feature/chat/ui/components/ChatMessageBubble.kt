package com.example.fitness_app.feature.chat.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitness_app.feature.chat.presentation.model.ChatMessageUi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun ChatMessageBubble(
    message: ChatMessageUi,
    onAddNutritionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.isFromUser

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .background(
                    color = if (isUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            if (message.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = message.imageUri),
                    contentDescription = "Изображение сообщения",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            if (message.imageUri != null && message.text.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    color = if (isUser) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (!isUser && message.nutritionInfo != null) {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Распознано КБЖУ",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "Калории: ${message.nutritionInfo.calories} ккал"
                        )
                        Text(
                            text = "Белки: ${message.nutritionInfo.proteins} г"
                        )
                        Text(
                            text = "Жиры: ${message.nutritionInfo.fats} г"
                        )
                        Text(
                            text = "Углеводы: ${message.nutritionInfo.carbs} г"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!message.isAddedToDiary && message.canAddToDiary) {
                    Button(
                        onClick = { onAddNutritionClick(message.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить в дневник")
                    }
                }

                if (message.isAddedToDiary) {
                    Text(
                        text = "Добавлено в дневной прогресс",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}