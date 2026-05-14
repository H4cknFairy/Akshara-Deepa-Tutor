package com.example.akshara_deepatutor.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.learningDataStore: DataStore<Preferences> by preferencesDataStore(name = "learning_prefs")

data class LastLearningSession(
    val subjectId: Int,
    val subjectName: String,
    val chapterName: String,
    val progress: Float
)

class LearningPreferences(private val context: Context) {
    companion object {
        private val SUBJECT_ID = intPreferencesKey("subject_id")
        private val SUBJECT_NAME = stringPreferencesKey("subject_name")
        private val CHAPTER_NAME = stringPreferencesKey("chapter_name")
        private val PROGRESS = floatPreferencesKey("progress")
    }

    val lastSession: Flow<LastLearningSession?> = context.learningDataStore.data
        .map { preferences ->
            val subjectName = preferences[SUBJECT_NAME]
            if (subjectName != null) {
                LastLearningSession(
                    subjectId = preferences[SUBJECT_ID] ?: 0,
                    subjectName = subjectName,
                    chapterName = preferences[CHAPTER_NAME] ?: "",
                    progress = preferences[PROGRESS] ?: 0f
                )
            } else {
                null
            }
        }

    suspend fun saveLastSession(subjectId: Int, subjectName: String, chapterName: String, progress: Float) {
        context.learningDataStore.edit { preferences ->
            preferences[SUBJECT_ID] = subjectId
            preferences[SUBJECT_NAME] = subjectName
            preferences[CHAPTER_NAME] = chapterName
            preferences[PROGRESS] = progress
        }
    }
}
