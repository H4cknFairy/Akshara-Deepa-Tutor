package com.example.akshara_deepatutor.ui.screens

import android.app.Application
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.data.ChapterEntity
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*
import com.example.akshara_deepatutor.ui.viewmodels.ChapterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterScreen(
    navController: NavHostController, 
    subjectId: Int,
    subjectName: String,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val viewModel: ChapterViewModel = viewModel(
        factory = ChapterViewModel.provideFactory(context.applicationContext as Application)
    )

    val chapters by viewModel.chapters.collectAsState()

    LaunchedEffect(subjectName) {
        if (subjectName.isNotEmpty()) {
            viewModel.loadChapters(subjectName)
        }
    }

    val completedCount = chapters.count { it.isCompleted }
    val totalChapters = chapters.size
    val progressPercentage = if (totalChapters > 0) completedCount.toFloat() / totalChapters else 0f

    val columns = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    // Adaptive Pastel Colors for Cards
    val isDark = isSystemInDarkTheme()
    val pastelColors = if (isDark) {
        listOf(
            Color(0xFF1A237E).copy(alpha = 0.4f),
            Color(0xFF4A148C).copy(alpha = 0.4f),
            Color(0xFF1B5E20).copy(alpha = 0.4f),
            Color(0xFF004D40).copy(alpha = 0.4f)
        )
    } else {
        listOf(
            Color(0xFFE3F2FD), // Light Blue
            Color(0xFFF3E5F5), // Soft Purple
            Color(0xFFFFF9C4), // Soft Cream
            Color(0xFFE0F7FA)  // Light Cyan
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subjectName.ifEmpty { "Chapters" }, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (chapters.isNotEmpty()) {
                BottomQuizSection(
                    buttonText = "Take Final $subjectName Quiz",
                    onStartQuiz = {
                        navController.navigate(Screen.Quiz.createRoute(-1, subjectName))
                    }
                )
            }
        }
    ) { paddingValues ->
        if (chapters.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading chapters...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item(span = { GridItemSpan(columns) }) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        MinimalProgressHeader(completedCount, totalChapters, progressPercentage)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Chapters",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                itemsIndexed(chapters, key = { _, chapter -> chapter.id }) { index, chapter ->
                    ChapterItem(
                        chapter = chapter,
                        backgroundColor = pastelColors[index % pastelColors.size],
                        onStart = {
                            viewModel.saveLearningProgress(
                                subjectId = subjectId,
                                subjectName = subjectName,
                                chapterName = chapter.name,
                                progress = progressPercentage
                            )
                            navController.navigate(Screen.ChapterContent.createRoute(chapter.id))
                        }
                    )
                }

                item(span = { GridItemSpan(columns) }) { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun MinimalProgressHeader(completed: Int, total: Int, progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "Progress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Overall Completion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$completed / $total Chapters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun ChapterItem(
    chapter: ChapterEntity, 
    backgroundColor: Color,
    onStart: () -> Unit
) {
    val isFullMarks = chapter.isCompleted && chapter.quizScore == 100
    // A chapter is considered "Failed" if it's not completed but has been attempted (score > 0)
    val isFailed = !chapter.isCompleted && chapter.quizScore > 0 && chapter.quizScore < 75
    
    // Animation for scale and color
    val scale by animateFloatAsState(
        targetValue = if (isFullMarks) 1.02f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "Scale"
    )

    val cardBgColor by animateColorAsState(
        targetValue = when {
            isFullMarks -> Color(0xFFE8F5E9)
            isFailed -> Color(0xFFFFEBEE) // Light Red background for failed
            else -> backgroundColor
        },
        animationSpec = tween(600),
        label = "BgColor"
    )

    val borderGlowColor by animateColorAsState(
        targetValue = when {
            isFullMarks -> SuccessGreen.copy(alpha = 0.5f)
            isFailed -> Color(0xFFD32F2F).copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Glow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFullMarks) 8.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = when {
                    isFullMarks -> SuccessGreen
                    isFailed -> Color(0xFFD32F2F)
                    else -> Color.Black
                },
                spotColor = when {
                    isFullMarks -> SuccessGreen
                    isFailed -> Color(0xFFD32F2F)
                    else -> Color.Black
                }
            )
            .border(
                width = if (isFullMarks || isFailed) 2.dp else 0.dp,
                color = borderGlowColor,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = when {
                        isFullMarks -> SuccessGreen
                        isFailed -> Color(0xFFEF5350)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (chapter.isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle, 
                            contentDescription = null, 
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isFullMarks) "Perfect Score!" else "Completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (isFailed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Error, 
                            contentDescription = null, 
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Incomplete: Needs higher score",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Gradient Start Button
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(25.dp), // Pill shape
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .height(40.dp)
                    .width(90.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = when {
                                chapter.isCompleted -> listOf(SuccessGreen, Color(0xFF81C784))
                                isFailed -> listOf(Color(0xFFD32F2F), Color(0xFFFF5252))
                                else -> listOf(BluePrimary, Color(0xFF64B5F6))
                            }
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
            ) {
                Text(
                    if (isFailed) "Retry" else "Start",
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BottomQuizSection(buttonText: String, onStartQuiz: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onStartQuiz,
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
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues()
            ) {
                Text(
                    buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}