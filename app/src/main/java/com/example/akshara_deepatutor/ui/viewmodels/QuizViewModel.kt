package com.example.akshara_deepatutor.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.akshara_deepatutor.data.AppDatabase
import com.example.akshara_deepatutor.data.AchievementManager
import com.example.akshara_deepatutor.data.LearningPreferences
import com.example.akshara_deepatutor.data.QuizRepository
import com.example.akshara_deepatutor.data.QuizResultEntity
import com.example.akshara_deepatutor.data.model.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val db by lazy { AppDatabase.getDatabase(application) }
    private val chapterDao by lazy { db.chapterDao() }
    private val quizDao by lazy { db.quizDao() }
    private val learningPreferences by lazy { LearningPreferences(application) }

    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    private val _finalScore = MutableStateFlow(0)
    val finalScore: StateFlow<Int> = _finalScore

    private var currentChapterName: String = "General Quiz"
    private var currentSubjectName: String = "General"
    private var currentSubjectId: Int = 0

    fun loadQuestions(chapterId: Int, subjectName: String) {
        viewModelScope.launch {
            if (chapterId != -1) {
                val chapter = chapterDao.getChapterById(chapterId)
                currentChapterName = chapter?.name ?: "Chapter Quiz"
                currentSubjectName = chapter?.subjectName ?: "General"
                
                currentSubjectId = when (currentSubjectName) {
                    "Science" -> 1
                    "Maths" -> 2
                    "English" -> 3
                    "Social" -> 4
                    else -> 0
                }
                
                val quizIdToUse = chapter?.quizId ?: chapterId
                _questions.value = QuizRepository.getQuestionsByChapter(quizIdToUse)
            } else if (subjectName != "none") {
                currentSubjectName = subjectName
                currentSubjectId = when (currentSubjectName) {
                    "Science" -> 1
                    "Maths" -> 2
                    "English" -> 3
                    "Social" -> 4
                    else -> 0
                }
                currentChapterName = "Final $subjectName Quiz"
                val quizIds = when (subjectName) {
                    "Science" -> listOf(1, 2, 3)
                    "Maths" -> listOf(4, 5, 6)
                    "English" -> listOf(7, 8, 9)
                    "Social" -> listOf(10, 11, 12)
                    else -> listOf(100)
                }
                _questions.value = QuizRepository.getQuestionsBySubject(quizIds, subjectName)
            }
        }
    }

    fun finishQuiz(score: Int, chapterId: Int) {
        viewModelScope.launch {
            _finalScore.value = score
            val total = _questions.value.size
            val achievementManager = AchievementManager(db)
            
            if (chapterId != -1) {
                // MARK COMPLETED ONLY IF SCORE IS 75% OR HIGHER
                val isPassed = score >= 75
                chapterDao.updateChapterQuizResult(chapterId, isPassed, score)
                
                // Recalculate progress for this subject
                val chapters = chapterDao.getChaptersForSubjectOnce(currentSubjectName)
                if (chapters.isNotEmpty()) {
                    // Progress counts only the PASSED chapters
                    val completedCount = chapters.count { it.isCompleted || (it.id == chapterId && isPassed) } 
                    val progress = completedCount.toFloat() / chapters.size
                    
                    learningPreferences.saveLastSession(
                        subjectId = currentSubjectId,
                        subjectName = currentSubjectName,
                        chapterName = currentChapterName,
                        progress = progress
                    )
                }

                // Check for new achievements (only if passed)
                if (isPassed) {
                    achievementManager.checkAndAwardAchievements(currentSubjectName)
                }
            } else if (currentSubjectName != "General") {
                // Final subject quiz completion check
                achievementManager.checkAndAwardAchievements(currentSubjectName)
            }
            
            // Save to Quiz History
            quizDao.insertQuizResult(
                QuizResultEntity(
                    chapterName = currentChapterName,
                    subjectName = currentSubjectName,
                    score = score,
                    totalQuestions = total,
                    percentage = score 
                )
            )

            _isFinished.value = true
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return QuizViewModel(application) as T
            }
        }
    }
}
