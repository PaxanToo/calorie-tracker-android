package com.example.fitness_app.feature.chat.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.feature.chat.presentation.ChatAction
import com.example.fitness_app.feature.chat.presentation.ChatViewModel
import java.io.File

@Composable
fun ChatScreen(
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val viewModel: ChatViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onAction(ChatAction.ImageSelected(uri))
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.onAction(ChatAction.ImageSelected(tempCameraImageUri))
        }
    }

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
            showImageSourceDialog = true
        },
        onClearSelectedImage = {
            viewModel.onAction(ChatAction.ClearSelectedImage)
        },
        onSendClick = {
            viewModel.onAction(ChatAction.SendMessage)
        }
    )

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выберите источник") },
            text = { Text("Откуда взять изображение?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                ) {
                    Text("Галерея")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showImageSourceDialog = false
                            val uri = createTempImageUri(context)
                            tempCameraImageUri = uri
                            takePictureLauncher.launch(uri)
                        }
                    ) {
                        Text("Камера")
                    }

                    TextButton(
                        onClick = {
                            showImageSourceDialog = false
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            }
        )
    }
}

private fun createTempImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images").apply {
        mkdirs()
    }

    val imageFile = File.createTempFile(
        "chat_camera_image_",
        ".jpg",
        imagesDir
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}