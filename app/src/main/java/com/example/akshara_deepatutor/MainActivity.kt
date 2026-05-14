package com.example.akshara_deepatutor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.akshara_deepatutor.data.ReminderPreferences
import com.example.akshara_deepatutor.data.ThemePreferences
import com.example.akshara_deepatutor.notifications.ReminderWorker
import com.example.akshara_deepatutor.ui.navigation.NavGraph
import com.example.akshara_deepatutor.ui.theme.AksharaDeepaTutorTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val themePreferences = ThemePreferences(this)
        val reminderPreferences = ReminderPreferences(this)
        
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)
            val notificationsEnabled by reminderPreferences.isNotificationsEnabled.collectAsState(initial = true)
            val reminderTime by reminderPreferences.reminderTime.collectAsState(initial = Pair(18, 0))

            LaunchedEffect(notificationsEnabled, reminderTime) {
                if (notificationsEnabled) {
                    ReminderWorker.scheduleReminder(this@MainActivity, reminderTime.first, reminderTime.second)
                }
            }
            
            AksharaDeepaTutorTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    windowWidthSizeClass = windowSizeClass.widthSizeClass,
                )
            }
        }
    }
}
