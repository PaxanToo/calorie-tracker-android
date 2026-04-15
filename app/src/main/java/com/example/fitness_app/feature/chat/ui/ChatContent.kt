package com.example.fitness_app.feature.chat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.fitness_app.feature.chat.presentation.ChatUiState
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi
import com.example.fitness_app.feature.chat.ui.components.ChatInputBar
import com.example.fitness_app.feature.chat.ui.components.ChatMessageBubble

@Composable
fun ChatContent(
    uiState: ChatUiState,
    contentPadding: PaddingValues,
    onInputChanged: (String) -> Unit,
    onModeSelected: (ChatMode) -> Unit,
    onGoalSelected: (NutritionGoalUi) -> Unit,
    onAttachClick: () -> Unit,
    onClearSelectedImage: () -> Unit,
    onSendClick: () -> Unit,
    onAddNutritionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var modeMenuExpanded by remember { mutableStateOf(false) }
    var goalMenuExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (modeMenuExpanded) 45f else 0f,
        animationSpec = tween(250),
        label = "mode_menu_rotation"
    )

    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        val extraItems = if (uiState.isLoading) 1 else 0
        val totalItems = uiState.messages.size + extraItems
        if (totalItems > 0) {
            listState.animateScrollToItem(totalItems - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Чат",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = when (uiState.selectedMode) {
                        ChatMode.DEFAULT -> "Обычный"
                        ChatMode.MEAL_CALORIES -> "Калории"
                        ChatMode.DISH_SUGGESTION -> {
                            when (uiState.selectedGoal) {
                                NutritionGoalUi.LOSE_WEIGHT -> "Подбор блюда · Похудение"
                                NutritionGoalUi.MAINTAIN_WEIGHT -> "Подбор блюда · Поддержание"
                                NutritionGoalUi.GAIN_WEIGHT -> "Подбор блюда · Набор массы"
                                null -> "Подбор блюда"
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        onAddNutritionClick = onAddNutritionClick
                    )
                }

                if (uiState.isLoading) {
                    item(key = "loading") {
                        CircularProgressIndicator()
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            AnimatedVisibility(visible = uiState.selectedImageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uiState.selectedImageUri),
                            contentDescription = "Выбранное фото",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Фото прикреплено",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Будет отправлено вместе с сообщением",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = onClearSelectedImage) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Убрать фото"
                            )
                        }
                    }
                }
            }

            ChatInputBar(
                inputText = uiState.inputText,
                isLoading = uiState.isLoading,
                onInputChanged = onInputChanged,
                onAttachClick = onAttachClick,
                onSendClick = onSendClick
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 58.dp, end = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(visible = modeMenuExpanded) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AssistChip(
                        onClick = {
                            onModeSelected(ChatMode.MEAL_CALORIES)
                            modeMenuExpanded = false
                            goalMenuExpanded = false
                        },
                        label = { Text("Узнать калории") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AssistChip(
                        onClick = {
                            onModeSelected(ChatMode.DISH_SUGGESTION)
                            modeMenuExpanded = false
                            goalMenuExpanded = true
                        },
                        label = { Text("Подобрать блюдо") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AssistChip(
                        onClick = {
                            onModeSelected(ChatMode.DEFAULT)
                            modeMenuExpanded = false
                            goalMenuExpanded = false
                        },
                        label = { Text("Сбросить режим") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            AnimatedVisibility(
                visible = goalMenuExpanded && uiState.selectedMode == ChatMode.DISH_SUGGESTION
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AssistChip(
                        onClick = {
                            onGoalSelected(NutritionGoalUi.LOSE_WEIGHT)
                            goalMenuExpanded = false
                        },
                        label = { Text("Похудение") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AssistChip(
                        onClick = {
                            onGoalSelected(NutritionGoalUi.MAINTAIN_WEIGHT)
                            goalMenuExpanded = false
                        },
                        label = { Text("Поддержание") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AssistChip(
                        onClick = {
                            onGoalSelected(NutritionGoalUi.GAIN_WEIGHT)
                            goalMenuExpanded = false
                        },
                        label = { Text("Набор массы") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            FloatingActionButton(
                onClick = { modeMenuExpanded = !modeMenuExpanded },
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Выбор режима",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}