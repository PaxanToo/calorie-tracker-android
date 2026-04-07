package com.example.fitness_app.feature.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitness_app.feature.chat.presentation.ChatUiState
import com.example.fitness_app.feature.chat.presentation.model.ChatMode
import com.example.fitness_app.feature.chat.presentation.model.NutritionGoalUi
import com.example.fitness_app.feature.chat.ui.components.ChatGoalSelector
import com.example.fitness_app.feature.chat.ui.components.ChatInputBar
import com.example.fitness_app.feature.chat.ui.components.ChatMessageBubble
import com.example.fitness_app.feature.chat.ui.components.ChatQuickActions
import com.example.fitness_app.feature.chat.ui.components.SelectedImageBar

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "AI Чат",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        ChatQuickActions(
            selectedMode = uiState.selectedMode,
            onModeSelected = onModeSelected
        )

        if (uiState.selectedMode != ChatMode.DEFAULT) {
            Spacer(modifier = Modifier.height(8.dp))

            AssistChip(
                onClick = { onModeSelected(ChatMode.DEFAULT) },
                label = { Text("Сбросить режим") }
            )
        }

        if (uiState.selectedMode == ChatMode.DISH_SUGGESTION) {
            Spacer(modifier = Modifier.height(12.dp))

            ChatGoalSelector(
                selectedGoal = uiState.selectedGoal,
                onGoalSelected = onGoalSelected
            )
        }

        if (uiState.selectedImageUri != null) {
            Spacer(modifier = Modifier.height(12.dp))

            SelectedImageBar(
                imageUri = uiState.selectedImageUri,
                onClearClick = onClearSelectedImage
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    onAddNutritionClick = onAddNutritionClick
                )
            }

            if (uiState.isLoading) {
                item {
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

        ChatInputBar(
            inputText = uiState.inputText,
            isLoading = uiState.isLoading,
            onInputChanged = onInputChanged,
            onAttachClick = onAttachClick,
            onSendClick = onSendClick
        )
    }
}