package com.example.akshara_deepatutor.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.akshara_deepatutor.data.AppDatabase
import com.example.akshara_deepatutor.data.ChapterEntity
import com.example.akshara_deepatutor.data.LastLearningSession
import com.example.akshara_deepatutor.data.LearningPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val chapterDao = db.chapterDao()
    private val quizDao = db.quizDao()
    private val learningPreferences = LearningPreferences(application)

    // Search Logic
    private val _searchQuery = MutableStateFlow("")
    
    @Suppress("unused")
    val searchQuery: StateFlow<String> = _searchQuery

    @Suppress("unused")
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Filtered Chapters for Search
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Suppress("unused")
    val searchResults: StateFlow<List<ChapterEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else chapterDao.getAllChapters().map { chapters ->
                chapters.filter { it.name.contains(query, ignoreCase = true) || it.subjectName.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Dynamically calculate progress for the last learning session from DB
    @OptIn(ExperimentalCoroutinesApi::class)
    val lastLearningSession: StateFlow<LastLearningSession?> = learningPreferences.lastSession
        .flatMapLatest { session ->
            if (session == null) {
                flowOf(null)
            } else {
                chapterDao.getChaptersForSubject(session.subjectName).map { chapters ->
                    if (chapters.isEmpty()) {
                        session
                    } else {
                        val completed = chapters.count { it.isCompleted }
                        val total = chapters.size
                        val dynamicProgress = if (total > 0) completed.toFloat() / total else 0f
                        session.copy(progress = dynamicProgress)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Data for the Overall Progress Card
    data class OverallProgress(val completed: Int, val total: Int, val progress: Float)

    val overallProgress: StateFlow<OverallProgress> = chapterDao.getAllChapters()
        .map { allChapters ->
            val completed = allChapters.count { it.isCompleted }
            val total = allChapters.size
            val progress = if (total > 0) completed.toFloat() / total else 0f
            OverallProgress(completed, total, progress)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OverallProgress(0, 0, 0f)
        )

    // Real data for the Subjects Section
    data class SubjectItemData(val id: Int, val name: String, val icon: ImageVector, val progress: Float, val chapters: Int)

    val subjectsProgress: StateFlow<List<SubjectItemData>> = chapterDao.getAllChapters()
        .map { allChapters ->
            val subjectsList = listOf("Science", "Maths", "English", "Social")
            val icons = mapOf(
                "Science" to Icons.Default.Science,
                "Maths" to Icons.Default.Functions,
                "English" to Icons.AutoMirrored.Filled.MenuBook,
                "Social" to Icons.Default.Public
            )
            val ids = mapOf("Science" to 1, "Maths" to 2, "English" to 3, "Social" to 4)

            subjectsList.map { subjectName ->
                val chapters = allChapters.filter { it.subjectName == subjectName }
                val completed = chapters.count { it.isCompleted }
                val total = chapters.size
                val progress = if (total > 0) completed.toFloat() / total else 0f
                SubjectItemData(
                    id = ids[subjectName] ?: 0,
                    name = subjectName,
                    icon = icons[subjectName] ?: Icons.Default.Book,
                    progress = progress,
                    chapters = total
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentQuizResults = quizDao.getAllQuizResults()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Weak Subject Analysis
    data class WeakSubjectData(val name: String, val avgScore: Int, val icon: ImageVector)

    val weakSubjects: StateFlow<List<WeakSubjectData>> = quizDao.getAllQuizResults()
        .map { results ->
            if (results.isEmpty()) return@map emptyList()

            val subjectsList = listOf("Science", "Maths", "English", "Social")
            val icons = mapOf(
                "Science" to Icons.Default.Science,
                "Maths" to Icons.Default.Functions,
                "English" to Icons.AutoMirrored.Filled.MenuBook,
                "Social" to Icons.Default.Public
            )

            subjectsList.mapNotNull { subject ->
                val subjectResults = results.filter { it.subjectName == subject }
                if (subjectResults.isNotEmpty()) {
                    val avg = subjectResults.map { it.percentage }.average().toInt()
                    // If average score is less than 60%, it's a weak area
                    if (avg < 60) {
                        WeakSubjectData(subject, avg, icons[subject] ?: Icons.Default.Book)
                    } else null
                } else null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(application) as T
            }
        }
    }
}
