package com.example.akshara_deepatutor.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.reminderDataStore: DataStore<Preferences> by preferencesDataStore(name = "reminder_prefs")

class ReminderPreferences(private val context: Context) {
    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val REMINDER_HOUR = androidx.datastore.preferences.core.intPreferencesKey("reminder_hour")
        private val REMINDER_MINUTE = androidx.datastore.preferences.core.intPreferencesKey("reminder_minute")
    }

    val isNotificationsEnabled: Flow<Boolean> = context.reminderDataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    val reminderTime: Flow<Pair<Int, Int>> = context.reminderDataStore.data
        .map { preferences ->
            val hour = preferences[REMINDER_HOUR] ?: 18 // Default 6 PM
            val minute = preferences[REMINDER_MINUTE] ?: 0
            Pair(hour, minute)
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.reminderDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.reminderDataStore.edit { preferences ->
            preferences[REMINDER_HOUR] = hour
            preferences[REMINDER_MINUTE] = minute
        }
    }
}
