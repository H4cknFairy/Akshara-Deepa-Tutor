package com.example.akshara_deepatutor.ui.screens

import android.app.Application
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*
import com.example.akshara_deepatutor.ui.viewmodels.HomeViewModel
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(context.applicationContext as Application),
    )

    val overallProgress by viewModel.overallProgress.collectAsState()
    val subjectsProgress by viewModel.subjectsProgress.collectAsState()
    val weakSubjects by viewModel.weakSubjects.collectAsState()
    val recentQuizzes by viewModel.recentQuizResults.collectAsState()

    var animationPlayed by remember { mutableStateOf(value = false) }
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact
    val columns = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = BluePrimary,
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("My Progress", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Reminders.route) }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Reminders")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
        bottomBar = {
            if (isCompact) {
                BottomNavigationBar(navController)
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item(span = { GridItemSpan(columns) }) { 
                Column {
                    Text(
                        text = "Your Learning Journey",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Keep pushing your limits! 🚀",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Radar Mastery Chart
            item(span = { GridItemSpan(columns) }) {
                RadarMasteryCard(subjectsProgress)
            }

            // Overall Progress Card
            item(span = { GridItemSpan(columns) }) {
                OverallProgressCard(
                    percentage = overallProgress.progress,
                    completed = overallProgress.completed,
                    total = overallProgress.total,
                    animate = animationPlayed,
                )
            }
            
            // ... (rest of the content remains)


            // Quiz Performance Quick Stats
            item(span = { GridItemSpan(columns) }) {
                if (recentQuizzes.isNotEmpty()) {
                    val avgScore = recentQuizzes.map { it.percentage }.average().toInt()
                    val lastScore = recentQuizzes.first().percentage
                    QuizPerformanceCard(
                        averageScore = avgScore,
                        lastScore = lastScore,
                    )
                }
            }

            // Weak Areas Analysis Section
            if (weakSubjects.isNotEmpty()) {
                item(span = { GridItemSpan(columns) }) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = "Subjects Needing Improvement ⚠️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color(0xFFFF8A80) else Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your average score in these subjects is below 60%. Try reviewing the chapters again.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(weakSubjects) { subject ->
                    WeakAreaCard(subject)
                }
            }

            item(span = { GridItemSpan(columns) }) {
                Text(
                    text = "Subject-wise Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(subjectsProgress) { data ->
                val color = when(data.name) {
                    "Science" -> Color(0xFF4CAF50)
                    "Maths" -> if (isSystemInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF2196F3)
                    "English" -> Color(0xFF9C27B0)
                    "Social" -> Color(0xFFFF9800)
                    else -> MaterialTheme.colorScheme.primary
                }
                SubjectProgressCard(
                    data = SubjectProgressData(data.name, data.icon, data.progress, color),
                    animate = animationPlayed
                )
            }

            item(span = { GridItemSpan(columns) }) { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun WeakAreaCard(subject: HomeViewModel.WeakSubjectData) {
    val isDark = isSystemInDarkTheme()
    val errorColor = if (isDark) Color(0xFFFF8A80) else Color(0xFFD32F2F)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF3E2723) else Color(0xFFFFF5F5),
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(errorColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(subject.icon, contentDescription = null, tint = errorColor, modifier = Modifier.size(28.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Current Average: ${subject.avgScore}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = errorColor,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = errorColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun OverallProgressCard(percentage: Float, completed: Int, total: Int, animate: Boolean) {
    val progress by animateFloatAsState(
        targetValue = if (animate) percentage else 0f,
        animationSpec = tween(1000),
        label = "ProgressAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2575FC), Color(0xFF6A11CB))
                    )
                )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${(progress * 100).toInt()}% Completed",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "$completed of $total chapters finished",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun QuizPerformanceCard(averageScore: Int, lastScore: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PerformanceItem("Average Score", "$averageScore%", Icons.Default.Stars, MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            PerformanceItem("Last Quiz", "$lastScore%", Icons.Default.Timer, Color(0xFFFFA000))
        }
    }
}

@Composable
fun PerformanceItem(label: String, value: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SubjectProgressCard(data: SubjectProgressData, animate: Boolean) {
    val progress by animateFloatAsState(
        targetValue = if (animate) data.progress else 0f,
        animationSpec = tween(1000),
        label = "SubjectProgressAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(data.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(data.icon, contentDescription = null, tint = data.color)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = data.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "${(progress * 100).toInt()}%", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = data.color,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
            }
        }
    }
}

data class SubjectProgressData(
    val name: String,
    val icon: ImageVector,
    val progress: Float,
    val color: Color
)

@Composable
fun RadarMasteryCard(subjects: List<HomeViewModel.SubjectItemData>) {
    if (subjects.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Subject Mastery Map",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                RadarChart(
                    data = subjects.map { it.progress },
                    labels = subjects.map { it.name },
                    color = BluePrimary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Visualizing your overall balance across subjects",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RadarChart(
    data: List<Float>,
    labels: List<String>,
    color: Color
) {
    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = center
        val radius = size.minDimension / 2
        val angleStep = (2 * Math.PI) / (data.size)

        // 1. Draw Background Polygons (Web)
        for (i in 1..4) {
            val factor = i / 4f
            val path = androidx.compose.ui.graphics.Path()
            for (j in data.indices) {
                val angle = j * angleStep - Math.PI / 2
                val x = center.x + cos(angle).toFloat() * radius * factor
                val y = center.y + sin(angle).toFloat() * radius * factor
                if (j == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, labelColor.copy(alpha = 0.1f), style = Stroke(width = 1.dp.toPx()))
        }

        // 2. Draw Axis Lines
        for (j in data.indices) {
            val angle = j * angleStep - Math.PI / 2
            val x = center.x + cos(angle).toFloat() * radius
            val y = center.y + sin(angle).toFloat() * radius
            drawLine(labelColor.copy(alpha = 0.1f), center, androidx.compose.ui.geometry.Offset(x, y), strokeWidth = 1.dp.toPx())
            
            // Draw Labels
            val labelRadius = radius + 20.dp.toPx()
            val lx = center.x + cos(angle).toFloat() * labelRadius
            val ly = center.y + sin(angle).toFloat() * labelRadius
            
            val textLayoutResult = textMeasurer.measure(
                text = labels[j],
                style = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = labelColor)
            )
            
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = androidx.compose.ui.geometry.Offset(
                    lx - textLayoutResult.size.width / 2,
                    ly - textLayoutResult.size.height / 2
                )
            )
        }

        // 3. Draw Data Path (Mastery)
        val dataPath = androidx.compose.ui.graphics.Path()
        for (j in data.indices) {
            val angle = j * angleStep - Math.PI / 2
            // Ensure at least 10% visibility for 0 progress
            val value = maxOf(data[j], 0.1f)
            val x = center.x + cos(angle).toFloat() * radius * value
            val y = center.y + sin(angle).toFloat() * radius * value
            if (j == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()
        
        drawPath(dataPath, color.copy(alpha = 0.3f))
        drawPath(dataPath, color, style = Stroke(width = 2.dp.toPx()))
    }
}

