package com.example.akshara_deepatutor.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.akshara_deepatutor.data.AppDatabase
import com.example.akshara_deepatutor.data.ChapterEntity
import com.example.akshara_deepatutor.data.LearningPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChapterViewModel(application: Application) : AndroidViewModel(application) {
    private val chapterDao by lazy { AppDatabase.getDatabase(application).chapterDao() }
    private val learningPreferences by lazy { LearningPreferences(application) }

    private val _chapters = MutableStateFlow<List<ChapterEntity>>(emptyList())
    val chapters: StateFlow<List<ChapterEntity>> = _chapters

    private var observeJob: kotlinx.coroutines.Job? = null

    fun loadChapters(subjectName: String) {
        if (subjectName.isBlank()) return
        
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            // Observe the database for changes automatically
            chapterDao.getChaptersForSubject(subjectName).collectLatest { existing ->
                if (existing.isEmpty()) {
                    val initialChapters = getInitialChapters(subjectName)
                    chapterDao.insertChapters(initialChapters)
                } else {
                    _chapters.value = existing
                }
            }
        }
    }

    fun saveLearningProgress(subjectId: Int, subjectName: String, chapterName: String, progress: Float) {
        viewModelScope.launch {
            learningPreferences.saveLastSession(subjectId, subjectName, chapterName, progress)
        }
    }

    private fun getInitialChapters(subjectName: String): List<ChapterEntity> {
        return when (subjectName) {
            "Science" -> listOf(
                ChapterEntity(name = "Living Things", subjectName = "Science", quizId = 1),
                ChapterEntity(name = "Matter", subjectName = "Science", quizId = 2),
                ChapterEntity(name = "Energy", subjectName = "Science", quizId = 3),
            )
            "Maths" -> listOf(
                ChapterEntity(name = "Algebra", subjectName = "Maths", quizId = 4),
                ChapterEntity(name = "Geometry", subjectName = "Maths", quizId = 5),
                ChapterEntity(name = "Trigonometry", subjectName = "Maths", quizId = 6),
            )
            "English" -> listOf(
                ChapterEntity(name = "Grammar", subjectName = "English", quizId = 7),
                ChapterEntity(name = "Vocabulary", subjectName = "English", quizId = 8),
                ChapterEntity(name = "Reading Skills", subjectName = "English", quizId = 9),
            )
            "Social" -> listOf(
                ChapterEntity(name = "History", subjectName = "Social", quizId = 10),
                ChapterEntity(name = "Geography", subjectName = "Social", quizId = 11),
                ChapterEntity(name = "Civics", subjectName = "Social", quizId = 12),
            )
            else -> listOf(
                ChapterEntity(name = "Introduction to $subjectName", subjectName = subjectName, quizId = 100),
                ChapterEntity(name = "Core Concepts", subjectName = subjectName, quizId = 101),
            )
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ChapterViewModel(application) as T
            }
        }
    }
}
