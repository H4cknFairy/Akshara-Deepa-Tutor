package com.example.akshara_deepatutor.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE id = :achievementId)")
    suspend fun hasAchievement(achievementId: String): Boolean
}
