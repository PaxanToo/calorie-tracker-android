package com.example.fitness_app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitness_app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {

    var startText by remember { mutableStateOf(false) }
    var showLogo by remember { mutableStateOf(false) }
    var startPulse by remember { mutableStateOf(false) }

    /* ---------------- TEXT MOVE ---------------- */

    val textOffset by animateDpAsState(
        targetValue = if (startText) 0.dp else 200.dp,
        animationSpec = tween(600),
        label = ""
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startText) 1f else 0f,
        animationSpec = tween(600),
        label = ""
    )

    /* ---------------- LOGO FADE ---------------- */

    val logoAlpha by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0f,
        animationSpec = tween(500),
        label = ""
    )

    /* ---------------- PULSE ---------------- */

    val scale by animateFloatAsState(
        targetValue = if (startPulse) 1.15f else 1f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    /* ---------------- TIMELINE ---------------- */

    LaunchedEffect(true) {

        delay(200)
        startText = true        // текст снизу

        delay(600)
        showLogo = true         // появляется логотип

        delay(700)
        startPulse = true       // пульсация

        delay(500)
        onFinished()            // переход
    }

    /* ---------------- UI ---------------- */

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        ) {

            Image(
                painter = painterResource(id = R.drawable.apple),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(140.dp)
                    .alpha(logoAlpha)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "FITNESS AI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(y = textOffset)
                    .alpha(textAlpha)
            )
        }
    }
}