package com.example.akshara_deepatutor.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.data.ReminderPreferences
import com.example.akshara_deepatutor.notifications.ReminderWorker
import com.example.akshara_deepatutor.ui.theme.BluePrimary
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val reminderPreferences = remember { ReminderPreferences(context) }
    val notificationsEnabled by reminderPreferences.isNotificationsEnabled.collectAsState(initial = true)
    val reminderTime by reminderPreferences.reminderTime.collectAsState(initial = Pair(18, 0))
    
    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact

    var showTimePicker by remember { mutableStateOf(value = false) }
    val timePickerState = rememberTimePickerState(
        initialHour = reminderTime.first,
        initialMinute = reminderTime.second,
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                reminderPreferences.setNotificationsEnabled(enabled = true)
                ReminderWorker.scheduleReminder(context, reminderTime.first, reminderTime.second)
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { /* No-op, dismissal handled by buttons */ },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            reminderPreferences.setReminderTime(timePickerState.hour, timePickerState.minute)
                            if (notificationsEnabled) {
                                ReminderWorker.scheduleReminder(context, timePickerState.hour, timePickerState.minute)
                            }
                            showTimePicker = false
                        }
                    },
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Reminders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Never miss a lesson",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Get daily notifications to keep your learning streak alive.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Daily Reminders", fontWeight = FontWeight.Bold)
                                Text(if (notificationsEnabled) "Turned On" else "Turned Off", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { enabled ->
                            if (enabled) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        scope.launch {
                                            reminderPreferences.setNotificationsEnabled(enabled = true)
                                            ReminderWorker.scheduleReminder(context, reminderTime.first, reminderTime.second)
                                        }
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    scope.launch {
                                        reminderPreferences.setNotificationsEnabled(enabled = true)
                                        ReminderWorker.scheduleReminder(context, reminderTime.first, reminderTime.second)
                                    }
                                }
                            } else {
                                scope.launch {
                                    reminderPreferences.setNotificationsEnabled(enabled = false)
                                    ReminderWorker.cancelReminder(context)
                                }
                            }
                        }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePicker = true }
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Reminder Time", fontWeight = FontWeight.Bold)
                                val hour = if (reminderTime.first > 12) reminderTime.first - 12 else if (reminderTime.first == 0) 12 else reminderTime.first
                                val amPm = if (reminderTime.first >= 12) "PM" else "AM"
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d %s", hour, reminderTime.second, amPm),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Text("Change", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    ReminderWorker.sendTestNotification(context)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Test Notification Now", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Reminders are sent daily at your preferred time to help you stay focused on your educational goals.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Reusable TimePickerDialog for older versions or missing native support
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable (() -> Unit),
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        title = { Text(text = title, style = MaterialTheme.typography.labelLarge) },
        text = { content() },
        modifier = Modifier.fillMaxWidth()
    )
}
