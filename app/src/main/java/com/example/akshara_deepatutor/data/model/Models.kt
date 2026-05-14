package com.example.akshara_deepatutor.data.model

data class Subject(
    val id: Int,
    val name: String,
    val icon: Int, // Drawable resource ID
    val progress: Float = 0f,
    val chapters: List<Chapter> = emptyList()
)

data class Chapter(
    val id: Int,
    val subjectName: String,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val quizScore: Int = 0
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val chapterId: Int
)
