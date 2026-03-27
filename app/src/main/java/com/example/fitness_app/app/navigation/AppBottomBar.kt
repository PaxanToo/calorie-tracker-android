package com.example.fitness_app.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(
    navController: NavController
) {
    val items = listOf(HomeDestination, ChatDestination)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .navigationBarsPadding()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            BottomBarItem(
                iconRes = item.iconId,
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(HomeDestination.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1f,
        animationSpec = tween(300),
        label = "navScale"
    )

    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.scale(scale),
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Gray
            }
        )
    }
}