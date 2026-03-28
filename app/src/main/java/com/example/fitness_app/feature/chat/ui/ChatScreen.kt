package com.example.fitness_app.feature.chat.ui

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.feature.chat.presentation.ChatAction
import com.example.fitness_app.feature.chat.presentation.ChatViewModel

@Composable
fun ChatScreen(
    contentPadding: PaddingValues = PaddingValues()
) {
    val viewModel: ChatViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    ChatContent(
        uiState = uiState,
        contentPadding = contentPadding,
        onInputChanged = { value ->
            viewModel.onAction(ChatAction.InputChanged(value))
        },
        onModeSelected = { mode ->
            viewModel.onAction(ChatAction.ModeSelected(mode))
        },
        onGoalSelected = { goal ->
            viewModel.onAction(ChatAction.GoalSelected(goal))
        },
        onAttachClick = {
            viewModel.onAction(
                ChatAction.ImageSelected(
                    Uri.parse("content://temp/mock_image")
                )
            )
        },
        onClearSelectedImage = {
            viewModel.onAction(ChatAction.ClearSelectedImage)
        },
        onSendClick = {
            viewModel.onAction(ChatAction.SendMessage)
        }
    )
}