package com.example.fitness_app.feature.chat.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitness_app.feature.chat.presentation.ChatAction
import com.example.fitness_app.feature.chat.presentation.ChatViewModel
import java.io.File
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay

@Composable
fun ChatScreen(
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val viewModel: ChatViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.showAchievementAnimation) {
        if (uiState.showAchievementAnimation) {
            delay(1800)
            viewModel.onAction(ChatAction.ConsumeAchievementAnimation)
        }
    }

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
        },
        onAddNutritionClick = { messageId ->
            viewModel.onAction(ChatAction.AddNutritionToDiary(messageId))
        }
    )

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выберите источник")

                    IconButton(
                        onClick = {
                            showImageSourceDialog = false
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-24).dp)
                    ) {
                        Text(
                            text = "×",
                            fontSize = 24.sp
                        )
                    }
                }
            },
            text = {
                Text("Откуда взять изображение?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImageSourceDialog = false
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.offset(x = (-12).dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB6FF00),
                        contentColor = Color.Black
                    )
                ) {
                    Text("Галерея",  color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showImageSourceDialog = false
                        val uri = createTempImageUri(context)
                        tempCameraImageUri = uri
                        takePictureLauncher.launch(uri)
                    },
                    modifier = Modifier.offset(x = (-60).dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB6FF00),
                        contentColor = Color.Black,
                    )
                ) {
                    Text("Камера",  color = Color.Black)
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