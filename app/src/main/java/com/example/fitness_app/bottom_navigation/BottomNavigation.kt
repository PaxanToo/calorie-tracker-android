package com.example.fitness_app.bottom_navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fitness_app.R


// Функция для нелинейного преобразования значения анимации.
// Используется для более плавного и естественного появления FAB-кнопок.
private fun easingTransform(
    from: Float,
    to: Float,
    value: Float
): Float {
    return FastOutSlowInEasing.transform(
        // Нормализация значения в диапазон от 0 до 1
        ((value - from) / (to - from)).coerceIn(0f, 1f)
    )
}


// Перегрузка оператора умножения для PaddingValues.
// Позволяет динамически масштабировать отступы в зависимости от анимации.
private operator fun PaddingValues.times(value: Float): PaddingValues =
    PaddingValues(
        top = calculateTopPadding() * value,
        bottom = calculateBottomPadding() * value,
        start = calculateStartPadding(LayoutDirection.Ltr) * value,
        end = calculateEndPadding(LayoutDirection.Ltr) * value
    )

// Основной composable нижней навигации приложения
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    // Отображаемые элементы в навиг. панели
    val items = listOf(Home, Page3)

    // Отслеживание текущего маршрута для подсветки активной иконки
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Состояние раскрытия FAB-группы
    var expanded by remember { mutableStateOf(false) }

    // Анимационное значение (0..1), управляющее раскрытием FAB-кнопок
    val animationProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "fabProgress"
    )

    // Контейнер для нижней навигации и FAB-группы
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        contentAlignment = Alignment.BottomCenter
    ) {

        // Группа плавающих кнопок (FAB)
        FabGroup(
            navController = navController,
            animationProgress = animationProgress,
            toggle = { expanded = !expanded }
        )

        // Нижняя панель навигации с иконками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route

                // Отрисовка отдельного элемента навигации
                BottomNavItem(
                    iconRes = item.iconId,
                    selected = selected
                ) {
                    navController.navigate(item.route) {
                        // Возврат к корневому экрану при навигации
                        popUpTo(Home.route)
                        // Исключаем повторное создание одного и того же экрана
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

// Группа анимированных FAB-кнопок, раскрывающихся из центральной кнопки
@Composable
private fun FabGroup(
    navController: NavController,
    animationProgress: Float,
    toggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        // FAB для перехода на экран профиля
        AnimatedFab(
            iconRes = R.drawable.profil,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 72.dp,
                    start = 180.dp
                ) * easingTransform(0f, 0.7f, animationProgress)
            ),
            opacity = animationProgress,
            onClick = {
                navController.navigate(Page2.route)
                toggle()
            }
        )

        // FAB для перехода на экран достижений
        AnimatedFab(
            iconRes = R.drawable.achiv,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 72.dp,
                    end = 180.dp
                ) * easingTransform(0.2f, 1f, animationProgress)
            ),
            opacity = animationProgress,
            onClick = {
                navController.navigate(Page4.route)
                toggle()
            }
        )

        // FAB для перехода на экран продуктов
        AnimatedFab(
            iconRes = R.drawable.xleb,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 92.dp,
                ) * easingTransform(0f, 0.7f, animationProgress)
            ),
            opacity = animationProgress,
            onClick = {
                navController.navigate(Page5.route)
                toggle()
            }
        )

        // Пустая FAB используется как визуальная подложка для анимации
        AnimatedFab(
            iconRes = null,
            modifier = Modifier.scale(1f - animationProgress * 0.25f)
        )

        // Центральная FAB-кнопка, управляющая раскрытием группы
        FloatingActionButton(
            onClick = toggle,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .scale(1.25f)
                .rotate(225 * animationProgress),
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

// Универсальный composable для анимированной FAB-кнопки
@Composable
private fun AnimatedFab(
    iconRes: Int?,
    modifier: Modifier,
    opacity: Float = 1f,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.scale(1.25f),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.secondary,
        elevation = FloatingActionButtonDefaults.elevation(0.dp)
    ) {
        iconRes?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                tint = Color.White.copy(alpha = opacity)
            )
        }
    }
}

// Отдельный элемент нижней навигации
@Composable
private fun BottomNavItem(
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Анимация увеличения иконки при выборе
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
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                Color.Gray
        )
    }
}
