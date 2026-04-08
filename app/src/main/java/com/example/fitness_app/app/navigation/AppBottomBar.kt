package com.example.fitness_app.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
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

    // Эти значения можешь сам крутить
    val barHeight = 88.dp
    val notchRadius = 42.dp
    val notchDepth = 30.dp
    val iconBottomPadding = 12.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(barHeight)
    ) {

        val barColor = Color.Red
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val width = size.width
            val height = size.height

            val notchRadiusPx = notchRadius.toPx()
            val notchDepthPx = notchDepth.toPx()
            val centerX = width / 2f

            val path = Path().apply {
                moveTo(0f, 24f)

                quadraticTo(0f, 0f, 24f, 0f)

                lineTo(centerX - notchRadiusPx - 18f, 0f)

                quadraticTo(
                    centerX - notchRadiusPx,
                    0f,
                    centerX - notchRadiusPx + 6f,
                    10f
                )

                cubicTo(
                    centerX - notchRadiusPx / 2f,
                    notchDepthPx,
                    centerX + notchRadiusPx / 2f,
                    notchDepthPx,
                    centerX + notchRadiusPx - 6f,
                    10f
                )

                quadraticTo(
                    centerX + notchRadiusPx,
                    0f,
                    centerX + notchRadiusPx + 18f,
                    0f
                )

                lineTo(width - 24f, 0f)

                quadraticTo(width, 0f, width, 24f)

                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = path,
                color = barColor,
                style = Fill
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 52.dp)
                .padding(bottom = iconBottomPadding)
                .offset(y = (-2).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (index == 1) {
                    Box(modifier = Modifier.weight(1f))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
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
    }
}

@Composable
private fun BottomBarItem(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.28f else 1.08f,
        animationSpec = tween(300),
        label = "navScale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(60.dp)
    ) {
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