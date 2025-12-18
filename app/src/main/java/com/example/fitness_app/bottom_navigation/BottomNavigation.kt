package com.example.fitness_app.bottom_navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp



@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        Home,
        Page2,
        Page3,
        Page4
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->

            val isSelected = currentRoute == item.route

            val iconSize by animateDpAsState(
                targetValue = if (isSelected) 30.dp else 22.dp,
                label = "iconSize"
            )

            val iconColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF4CAF50) else Color.Gray,
                label = "iconColor"
            )



            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Home.route)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = item.title,
                        modifier = Modifier.size(iconSize),
                        tint = iconColor
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 10.sp, color = iconColor)
                }
            )
        }
    }
}
