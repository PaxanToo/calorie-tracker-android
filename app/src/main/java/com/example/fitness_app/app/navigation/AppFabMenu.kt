package com.example.fitness_app.app.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.border
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
import com.example.fitness_app.ui.theme.BackgroundBlack
import com.example.fitness_app.ui.theme.GrayBorder

@Composable
fun AppFabMenu(
    navController: NavController
) {
    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(
        targetState = expanded,
        label = "fabMenuTransition"
    )

    val mainFabRotation by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "mainFabRotation"
    ) { isExpanded ->
        if (isExpanded) 225f else 0f
    }

    val mainFabScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 260, easing = FastOutSlowInEasing)
        },
        label = "mainFabScale"
    ) { isExpanded ->
        if (isExpanded) 1.32f else 1.4f
    }

    val topOffsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 420, easing = FastOutSlowInEasing)
        },
        label = "topOffsetY"
    ) { isExpanded ->
        if (isExpanded) (-82).dp else 0.dp
    }

    val topScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 420, easing = FastOutSlowInEasing)
        },
        label = "topScale"
    ) { isExpanded ->
        if (isExpanded) 1f else 0.7f
    }

    val topAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300)
        },
        label = "topAlpha"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    val leftOffsetX by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 40, easing = FastOutSlowInEasing)
        },
        label = "leftOffsetX"
    ) { isExpanded ->
        if (isExpanded) (-72).dp else 0.dp
    }

    val leftOffsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 40, easing = FastOutSlowInEasing)
        },
        label = "leftOffsetY"
    ) { isExpanded ->
        if (isExpanded) (-38).dp else 0.dp
    }

    val leftScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 40, easing = FastOutSlowInEasing)
        },
        label = "leftScale"
    ) { isExpanded ->
        if (isExpanded) 1f else 0.7f
    }

    val leftAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 320, delayMillis = 40)
        },
        label = "leftAlpha"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    val rightOffsetX by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 80, easing = FastOutSlowInEasing)
        },
        label = "rightOffsetX"
    ) { isExpanded ->
        if (isExpanded) 72.dp else 0.dp
    }

    val rightOffsetY by transition.animateDp(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 80, easing = FastOutSlowInEasing)
        },
        label = "rightOffsetY"
    ) { isExpanded ->
        if (isExpanded) (-38).dp else 0.dp
    }

    val rightScale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 460, delayMillis = 80, easing = FastOutSlowInEasing)
        },
        label = "rightScale"
    ) { isExpanded ->
        if (isExpanded) 1f else 0.7f
    }

    val rightAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 320, delayMillis = 80)
        },
        label = "rightAlpha"
    ) { isExpanded ->
        if (isExpanded) 1f else 0f
    }

    val mainBorderColor = if (expanded) {
        GrayBorder
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box {
        AnimatedFab(
            iconRes = R.drawable.profil,
            modifier = Modifier
                .offset(x = leftOffsetX, y = leftOffsetY)
                .scale(leftScale)
                .alpha(leftAlpha),
            onClick = {
                navController.navigate(ProfileDestination.route)
                expanded = false
            }
        )

        AnimatedFab(
            iconRes = R.drawable.achiv,
            modifier = Modifier
                .offset(y = topOffsetY)
                .scale(topScale)
                .alpha(topAlpha),
            onClick = {
                navController.navigate(AchievementsDestination.route)
                expanded = false
            }
        )

        AnimatedFab(
            iconRes = R.drawable.xleb,
            modifier = Modifier
                .offset(x = rightOffsetX, y = rightOffsetY)
                .scale(rightScale)
                .alpha(rightAlpha),
            onClick = {
                navController.navigate(FoodDestination.route)
                expanded = false
            }
        )

        FloatingActionButton(
            onClick = { expanded = !expanded },
            shape = CircleShape,
            containerColor = BackgroundBlack,
            modifier = Modifier
                .offset(y = 26.dp)
                .scale(mainFabScale)
                .rotate(mainFabRotation)
                .border(
                    width = 2.dp,
                    color = mainBorderColor,
                    shape = CircleShape
                ),
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