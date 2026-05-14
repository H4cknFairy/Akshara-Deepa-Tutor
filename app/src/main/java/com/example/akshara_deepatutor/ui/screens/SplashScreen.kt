package com.example.akshara_deepatutor.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.R
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.BluePrimary
import com.example.akshara_deepatutor.ui.theme.BlueSecondary
import com.example.akshara_deepatutor.ui.theme.BlueTertiary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {

    // Animation states
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    // Animation + Navigation logic
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200)
            )
        }

        delay(3000) // 3 seconds total splash time

        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }

    // Modern Gradient Background with branding colors
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White,
            BlueTertiary.copy(alpha = 0.5f),
            BlueTertiary
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        // Soft Blue Glow Effect (Background)
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(scale.value)
                .alpha(alpha.value * 0.7f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BlueSecondary.copy(alpha = 0.6f),
                            BlueSecondary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Centered Logo with White Background Circle
            Surface(
                modifier = Modifier
                    .size(180.dp) // Slightly smaller to accommodate text
                    .scale(scale.value)
                    .alpha(alpha.value)
                    .shadow(
                        elevation = 25.dp,
                        shape = CircleShape,
                        spotColor = BlueSecondary.copy(alpha = 0.5f),
                        ambientColor = BlueSecondary.copy(alpha = 0.5f)
                    ),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.my_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with Animation
            Text(
                text = "Akshara-Deepa",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = BluePrimary,
                modifier = Modifier
                    .alpha(alpha.value)
                    .scale(scale.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Illuminate Your Learning Journey",
                style = MaterialTheme.typography.bodyMedium,
                color = BluePrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(alpha.value),
                textAlign = TextAlign.Center
            )
        }

        // Bottom Loading Indicator
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(36.dp)
                .alpha(alpha.value),
            color = BluePrimary,
            strokeWidth = 4.dp
        )
    }
}
