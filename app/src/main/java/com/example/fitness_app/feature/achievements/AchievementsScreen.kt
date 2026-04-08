package com.example.fitness_app.feature.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.fitness_app.R
import com.example.fitness_app.core.datastore.PrefsKeys
import com.example.fitness_app.core.datastore.prefsDataStore

private data class AchievementUi(
    val title: String,
    val description: String,
    val unlocked: Boolean
)

@Composable
fun AchievementsScreen() {
    val context = LocalContext.current
    val prefs by context.prefsDataStore().data.collectAsState(initial = null)

    val achievements = buildAchievements(prefs)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Достижения",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        items(achievements) { achievement ->
            AchievementCard(achievement = achievement)
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (achievement.unlocked) {
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (achievement.unlocked) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

private fun buildAchievements(prefs: Preferences?): List<AchievementUi> {
    val totalGoalCompletions = prefs?.get(PrefsKeys.ACH_TOTAL_GOAL_COMPLETIONS) ?: 0
    val firstProductAdded = prefs?.get(PrefsKeys.ACH_FIRST_PRODUCT_ADDED) ?: false
    val aiMealAdded = prefs?.get(PrefsKeys.ACH_AI_MEAL_ADDED) ?: false

    return listOf(
        AchievementUi(
            title = "Первый шаг",
            description = "Выполнить дневную цель 1 раз",
            unlocked = totalGoalCompletions >= 1
        ),
        AchievementUi(
            title = "На правильном пути",
            description = "Выполнить дневную цель 3 раза",
            unlocked = totalGoalCompletions >= 3
        ),
        AchievementUi(
            title = "Стабильность",
            description = "Выполнить дневную цель 7 раз",
            unlocked = totalGoalCompletions >= 7
        ),
        AchievementUi(
            title = "Первый продукт",
            description = "Добавить еду через экран продуктов",
            unlocked = firstProductAdded
        ),
        AchievementUi(
            title = "AI-помощник",
            description = "Добавить блюдо в дневник из AI-чата",
            unlocked = aiMealAdded
        )
    )
}