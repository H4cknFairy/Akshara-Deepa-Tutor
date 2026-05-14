package com.example.akshara_deepatutor.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE subjectName = :subjectName")
    fun getChaptersForSubject(subjectName: String): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Query("UPDATE chapters SET isCompleted = :completed, quizScore = :score WHERE id = :chapterId")
    suspend fun updateChapterQuizResult(chapterId: Int, completed: Boolean, score: Int)

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: Int): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE subjectName = :subjectName")
    suspend fun getChaptersForSubjectOnce(subjectName: String): List<ChapterEntity>

    @Query("SELECT * FROM chapters WHERE isCompleted = 1")
    fun getCompletedChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters")
    fun getAllChapters(): Flow<List<ChapterEntity>>
}
