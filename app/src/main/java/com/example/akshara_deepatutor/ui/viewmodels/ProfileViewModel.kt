package com.example.akshara_deepatutor.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.akshara_deepatutor.data.AchievementEntity
import com.example.akshara_deepatutor.data.AppDatabase
import com.example.akshara_deepatutor.data.ChapterEntity
import com.example.akshara_deepatutor.data.QuizResultEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db by lazy { AppDatabase.getDatabase(application) }
    private val chapterDao by lazy { db.chapterDao() }
    private val quizDao by lazy { db.quizDao() }
    private val achievementDao by lazy { db.achievementDao() }

    private val _completedChapters = MutableStateFlow<List<ChapterEntity>>(emptyList())
    val completedChapters: StateFlow<List<ChapterEntity>> = _completedChapters

    private val _quizHistory = MutableStateFlow<List<QuizResultEntity>>(emptyList())
    val quizHistory: StateFlow<List<QuizResultEntity>> = _quizHistory

    private val _achievements = MutableStateFlow<List<AchievementEntity>>(emptyList())
    val achievements: StateFlow<List<AchievementEntity>> = _achievements

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints

    private val _averageScore = MutableStateFlow(0)
    val averageScore: StateFlow<Int> = _averageScore

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            chapterDao.getCompletedChapters().collect { list ->
                _completedChapters.value = list
            }
        }
        viewModelScope.launch {
            achievementDao.getAllAchievements().collect { list ->
                _achievements.value = list
            }
        }
        viewModelScope.launch {
            quizDao.getAllQuizResults().collect { list ->
                _quizHistory.value = list
                // Calculate points (e.g., 10 points per 1% score)
                _totalPoints.value = list.sumOf { it.percentage * 10 }
                // Calculate average percentage
                if (list.isNotEmpty()) {
                    _averageScore.value = list.sumOf { it.percentage } / list.size
                }
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(application) as T
            }
        }
    }
}
