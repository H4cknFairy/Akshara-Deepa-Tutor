package com.example.akshara_deepatutor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterName: String,
    val subjectName: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Int,
    val timestamp: Long = System.currentTimeMillis(),
)
