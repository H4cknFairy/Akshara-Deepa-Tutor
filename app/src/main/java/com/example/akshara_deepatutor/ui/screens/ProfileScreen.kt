package com.example.akshara_deepatutor.ui.screens

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.data.ReminderPreferences
import com.example.akshara_deepatutor.data.ThemePreferences
import com.example.akshara_deepatutor.notifications.ReminderWorker
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*
import com.example.akshara_deepatutor.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    val reminderPreferences = remember { ReminderPreferences(context) }
    
    val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)
    val notificationsEnabled by reminderPreferences.isNotificationsEnabled.collectAsState(initial = true)
    
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(context.applicationContext as Application)
    )
    
    val completedCount by profileViewModel.completedChapters.collectAsState()
    val averageScore by profileViewModel.averageScore.collectAsState()
    val totalPoints by profileViewModel.totalPoints.collectAsState()
    val achievements by profileViewModel.achievements.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAchievementsDialog by remember { mutableStateOf(false) }

    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact

    // Permission launcher for notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                reminderPreferences.setNotificationsEnabled(true)
                ReminderWorker.scheduleReminder(context)
            }
        }
    }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout from Akshara-Deepa Tutor?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Confirm", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About App") },
            text = { 
                Column {
                    Text("Akshara-Deepa Tutor", fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Empowering students through accessible and interactive learning. Developed with ❤️ for education.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showAchievementsDialog) {
        AlertDialog(
            onDismissRequest = { showAchievementsDialog = false },
            title = { Text("Achievements") },
            text = { 
                if (achievements.isEmpty()) {
                    Text("Complete chapters and quizzes to earn badges!", color = Color.Gray)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        achievements.forEach { achievement ->
                            val icon = when (achievement.iconName) {
                                "star" -> Icons.Default.Star
                                "school" -> Icons.Default.School
                                "local_fire_department" -> Icons.Default.LocalFireDepartment
                                else -> Icons.Default.EmojiEvents
                            }
                            AchievementItem(icon, achievement.title, achievement.description)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAchievementsDialog = false }) {
                    Text("Awesome!")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (isCompact) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Profile Header
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + expandVertically()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier
                                .size(110.dp)
                                .shadow(8.dp, CircleShape),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "RS", 
                                    fontSize = 36.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp),
                            shape = CircleShape,
                            color = Color(0xFFFFD700),
                            tonalElevation = 4.dp
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.padding(6.dp), tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Ranjitha S", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Student | Grade 10", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("🔥 5 Day Learning Streak", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "\"Knowledge is the lamp that lights the way to success.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // 2. Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(Modifier.weight(1f), "${completedCount.size}", "Completed", Icons.Default.CheckCircle, Color(0xFFE8F5E9), Color(0xFF4CAF50))
                StatCard(Modifier.weight(1f), "$averageScore%", "Avg. Score", Icons.Default.Percent, Color(0xFFE3F2FD), Color(0xFF2196F3))
                StatCard(Modifier.weight(1f), "$totalPoints", "Points", Icons.Default.EmojiEvents, Color(0xFFFFF3E0), Color(0xFFFF9800))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Settings Groups
            SettingsGroup("Account & Progress") {
                ProfileOptionItem(Icons.Default.Person, "Edit Profile", "Update your info", onClick = { navController.navigate(Screen.EditProfile.route) })
                ProfileOptionItem(Icons.Default.Timeline, "Learning Progress", "View detailed stats", onClick = { navController.navigate(Screen.Dashboard.route) })
                ProfileOptionItem(Icons.Default.LibraryBooks, "Completed Chapters", "Review your history", onClick = { navController.navigate(Screen.CompletedChapters.route) })
                ProfileOptionItem(Icons.Default.History, "Quiz History", "Check past scores", onClick = { navController.navigate(Screen.QuizHistory.route) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            SettingsGroup("Preferences & App") {
                ProfileOptionItem(
                    icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Dark Mode",
                    subtitle = if (isDarkMode) "Currently Dark" else "Currently Light",
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { scope.launch { themePreferences.saveTheme(it) } }
                        )
                    },
                    onClick = { scope.launch { themePreferences.saveTheme(!isDarkMode) } }
                )
                ProfileOptionItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "App alerts & updates",
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                            scope.launch {
                                                reminderPreferences.setNotificationsEnabled(true)
                                                ReminderWorker.scheduleReminder(context)
                                            }
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    } else {
                                        scope.launch {
                                            reminderPreferences.setNotificationsEnabled(true)
                                            ReminderWorker.scheduleReminder(context)
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        reminderPreferences.setNotificationsEnabled(false)
                                        ReminderWorker.cancelReminder(context)
                                    }
                                }
                            }
                        )
                    },
                    onClick = { /* Toggle via Switch */ }
                )
                ProfileOptionItem(Icons.Default.EmojiEvents, "Achievements", "View badges & rewards", onClick = { showAchievementsDialog = true })
                ProfileOptionItem(Icons.Default.Info, "About App", "App version & details", onClick = { showAboutDialog = true })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Logout
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color(0xFF3B1E1E) else Color(0xFFFFEBEE)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "Scale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, value: String, label: String, icon: ImageVector, bgColor: Color, tint: Color) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = tint)
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = tint.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun AchievementItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFFFD700).copy(alpha = 0.2f)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
