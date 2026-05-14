package com.example.akshara_deepatutor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String, // e.g., "science_whiz"
    val title: String,
    val description: String,
    val iconName: String, // To map to an icon
    val earnedDate: Long = System.currentTimeMillis()
)
