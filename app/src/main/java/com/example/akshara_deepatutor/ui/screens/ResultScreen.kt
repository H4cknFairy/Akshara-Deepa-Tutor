package com.example.akshara_deepatutor.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*

@Composable
fun ResultScreen(navController: NavHostController, score: Int) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    // Performance logic
    val performance = when {
        score >= 90 -> PerformanceData("Excellent Work! 🌟", "You've mastered this topic!", SuccessGreen)
        score >= 70 -> PerformanceData("Great Job! 👍", "You have a strong understanding.", BluePrimary)
        score >= 50 -> PerformanceData("Good Effort 🙂", "A bit more practice will help.", Color(0xFFFFA000))
        else -> PerformanceData("Keep Practicing 📚", "Don't give up, try again!", Color(0xFFD32F2F))
    }

    val animatedScore by animateFloatAsState(
        targetValue = if (animationPlayed) score.toFloat() / 100 else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Title Section
        Text(
            text = "Quiz Completed 🎉",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Circular Progress Section
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            CircularProgressIndicator(
                progress = { animatedScore },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 12.dp,
                color = performance.color,
                trackColor = performance.color.copy(alpha = 0.1f),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animatedScore * 100).toInt()}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = performance.color
                )
                Text(
                    text = "Score",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Performance Message (Directly on screen, no background)
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = performance.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = performance.color
            )
            Text(
                text = performance.message,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Details Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DetailCard(
                modifier = Modifier.weight(1f),
                title = "Correct",
                value = "${(score * 5 / 100)}", // Assuming 5 questions total
                icon = Icons.Default.CheckCircle,
                iconColor = SuccessGreen
            )
            DetailCard(
                modifier = Modifier.weight(1f),
                title = "Wrong",
                value = "${5 - (score * 5 / 100)}",
                icon = Icons.Default.Cancel,
                iconColor = Color(0xFFD32F2F)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DetailCard(
                modifier = Modifier.weight(1f),
                title = "Total",
                value = "5",
                icon = Icons.AutoMirrored.Filled.Assignment,
                iconColor = BluePrimary
            )
            DetailCard(
                modifier = Modifier.weight(1f),
                title = "Points",
                value = "${score * 10}",
                icon = Icons.Default.EmojiEvents,
                iconColor = Color(0xFFFFA000)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Review Section Header
        Text(
            text = "Mastery Review",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Mastery Review Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ReviewItem("Conceptual Understanding", if (score >= 80) "Excellent" else "Good", SuccessGreen)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                ReviewItem("Retention Speed", "Fast", BluePrimary)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                ReviewItem("Subject Accuracy", "$score%", if (score >= 60) SuccessGreen else Color(0xFFD32F2F))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(40.dp))

        // Action Buttons
        Button(
            onClick = { navController.navigate(Screen.Quiz.route) {
                popUpTo(Screen.Quiz.route) { inclusive = true }
            } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BluePrimary, Color(0xFF64B5F6))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Retry Quiz", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text("Back to Home", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ReviewItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun DetailCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier.shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class PerformanceData(
    val title: String,
    val message: String,
    val color: Color
)
