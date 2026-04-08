package com.example.fitness_app.app.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitness_app.R

@Composable
fun AppFabMenu(
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    val animationProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "fabProgress"
    )

    Box {
        AnimatedFab(
            iconRes = R.drawable.profil,
            modifier = Modifier
                .offset(x = (-80).dp * animationProgress, y = (-40).dp * animationProgress)
                .alpha(animationProgress),
            onClick = {
                navController.navigate(ProfileDestination.route)
                expanded = false
            }
        )

        AnimatedFab(
            iconRes = R.drawable.achiv,
            modifier = Modifier
                .offset(y = (-90).dp * animationProgress)
                .alpha(animationProgress),
            onClick = {
                navController.navigate(AchievementsDestination.route)
                expanded = false
            }
        )

        AnimatedFab(
            iconRes = R.drawable.xleb,
            modifier = Modifier
                .offset(x = 80.dp * animationProgress, y = (-40).dp * animationProgress)
                .alpha(animationProgress),
            onClick = {
                navController.navigate(FoodDestination.route)
                expanded = false
            }
        )

        FloatingActionButton(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .offset(y = 26.dp)
                .scale(1.4f)
                .rotate(225f * animationProgress),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AnimatedFab(
    iconRes: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.secondary,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.White
        )
    }
}