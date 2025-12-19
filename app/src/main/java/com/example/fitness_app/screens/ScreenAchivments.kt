package com.example.fitness_app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.fitness_app.R
import com.example.fitness_app.DATA.PrefsKeys
import com.example.fitness_app.DATA.prefsDataStore
import kotlinx.coroutines.flow.first

@Composable
fun ScreenAchievements() {

    val context = LocalContext.current
    var unlocked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.prefsDataStore().data.first()
        unlocked = prefs[PrefsKeys.ACH_GOAL_REACHED] ?: false
    }


    val achievements = listOf(
        "Выполните поставленную цель 1 раз"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 35.dp, start = 20.dp, end = 20.dp, bottom = 30.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(achievements) { achievementText ->

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = achievementText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (unlocked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                if (unlocked) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.lottie)
                    )
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.vopros),
                        contentDescription = "Achievement locked",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
