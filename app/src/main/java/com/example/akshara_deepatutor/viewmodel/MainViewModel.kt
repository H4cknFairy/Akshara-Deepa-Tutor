package com.example.akshara_deepatutor.viewmodel

import androidx.lifecycle.ViewModel
import com.example.akshara_deepatutor.data.model.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("unused")
class MainViewModel : ViewModel() {
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    @Suppress("unused")
    val subjects: StateFlow<List<Subject>> = _subjects

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        _subjects.value = listOf(
            Subject(1, "Mathematics", 0, 0.4f),
            Subject(2, "Science", 0, 0.6f),
            Subject(3, "History", 0, 0.2f),
            Subject(4, "English", 0, 0.8f),
        )
    }
}
