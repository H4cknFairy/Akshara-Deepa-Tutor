package com.example.akshara_deepatutor.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.ui.components.SubjectCard
import com.example.akshara_deepatutor.ui.navigation.Screen

data class SubjectUIModel(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val progress: Float,
    val chapters: Int,
    val lightBgColor: Color,
    val darkBgColor: Color,
    val lightAccentColor: Color,
    val darkAccentColor: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val isDark = isSystemInDarkTheme()
    
    val subjects = listOf(
        SubjectUIModel(1, "Science", Icons.Default.Science, 0.45f, 15, Color(0xFFE8F5E9), Color(0xFF1B5E20), Color(0xFF4CAF50), Color(0xFF81C784)),
        SubjectUIModel(2, "Maths", Icons.Default.Functions, 0.70f, 22, Color(0xFFE3F2FD), Color(0xFF0D47A1), Color(0xFF0D47A1), Color(0xFF64B5F6)),
        SubjectUIModel(3, "English", Icons.AutoMirrored.Filled.MenuBook, 0.30f, 10, Color(0xFFF3E5F5), Color(0xFF4A148C), Color(0xFF9C27B0), Color(0xFFBA68C8)),
        SubjectUIModel(4, "Social", Icons.Default.Public, 0.55f, 18, Color(0xFFFFF3E0), Color(0xFFE65100), Color(0xFFFF9800), Color(0xFFFFB74D))
    )

    var isVisible by remember { mutableStateOf(value = false) }
    LaunchedEffect(Unit) { isVisible = true }

    val columns = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> 1
        WindowWidthSizeClass.Medium -> 2
        else -> 3
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Subjects", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(subjects) { index, subject ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = index * 100)) +
                                slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(durationMillis = 500, delayMillis = index * 100))
                    ) {
                        SubjectCard(
                            name = subject.name,
                            icon = subject.icon,
                            progress = subject.progress,
                            chapterCount = subject.chapters,
                            backgroundColor = if (isDark) subject.darkBgColor.copy(alpha = 0.3f) else subject.lightBgColor,
                            accentColor = if (isDark) subject.darkAccentColor else subject.lightAccentColor,
                            onClick = {
                                navController.navigate(Screen.Chapter.createRoute(subject.id, subject.name))
                            }
                        )
                    }
                }
            }
        }
    }
}