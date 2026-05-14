package com.example.akshara_deepatutor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val subjectName: String,
    val isCompleted: Boolean = false,
    val quizId: Int = 0,
    val quizScore: Int = 0 // Storing the latest score
)
