package com.example.akshara_deepatutor.data

class AchievementManager(private val db: AppDatabase) {

    suspend fun checkAndAwardAchievements(subjectName: String) {
        val chapterDao = db.chapterDao()
        val achievementDao = db.achievementDao()

        // 1. Check for "First Steps" (Completed any chapter)
        if (!achievementDao.hasAchievement("first_steps")) {
            val allChapters = chapterDao.getChaptersForSubjectOnce(subjectName)
            if (allChapters.any { it.isCompleted }) {
                achievementDao.insertAchievement(
                    AchievementEntity("first_steps", "First Steps", "Completed your first chapter!", "star"),
                )
            }
        }

        // 2. Check for Subject Completion (100%)
        val subjectChapters = chapterDao.getChaptersForSubjectOnce(subjectName)
        if (subjectChapters.isNotEmpty() && subjectChapters.all { it.isCompleted }) {
            val achievementId = "${subjectName.lowercase()}_whiz"
            if (!achievementDao.hasAchievement(achievementId)) {
                achievementDao.insertAchievement(
                    AchievementEntity(
                        achievementId,
                        "$subjectName Whiz",
                        "Mastered all chapters in $subjectName!",
                        "school"
                    )
                )
            }
        }
        
        // 3. Check for "Grand Master" (All subjects 100%)
        // This could be expensive, so maybe only check if a subject whiz was just awarded
    }
}
