package com.example.akshara_deepatutor.util

object ProgressUtils {
    /**
     * Calculates the percentage from a list of quiz percentages.
     * Returns 0 if the list is empty.
     */
    fun calculateAverage(percentages: List<Int>): Int {
        if (percentages.isEmpty()) return 0
        return percentages.average().toInt()
    }

    /**
     * Determines if a subject is "weak" based on an average threshold (default 60%).
     */
    fun isWeakSubject(avgScore: Int, threshold: Int = 60): Boolean {
        return avgScore < threshold && avgScore > 0
    }

    /**
     * AI-Powered Readiness Score: Uses weighted averages and time-decay.
     * Higher weight for recent scores, penalty for long gaps in study.
     */
    fun calculateReadinessScore(recentScores: List<Int>, lastAttemptTimestamp: Long): Int {
        if (recentScores.isEmpty()) return 0
        
        // 1. Weighted Average (Recent scores matter more - AI weighting)
        val weightedSum = recentScores.mapIndexed { index, score -> 
            score * (index + 1) 
        }.sum()
        val weightDivider = (1..recentScores.size).sum()
        val weightedAvg = weightedSum.toFloat() / weightDivider

        // 2. Time Decay (Simulating forgetting curve)
        val daysSinceLastAttempt = (System.currentTimeMillis() - lastAttemptTimestamp) / (1000 * 60 * 60 * 24)
        val decayFactor = when {
            daysSinceLastAttempt <= 1 -> 1.0f  // Fresh
            daysSinceLastAttempt <= 3 -> 0.9f  // Slightly faded
            daysSinceLastAttempt <= 7 -> 0.7f  // Fading
            else -> 0.5f                       // Significant forgetting
        }

        return (weightedAvg * decayFactor).toInt().coerceIn(0, 100)
    }

    /**
     * Formats a progress float (0.0 - 1.0) into a display percentage string.
     */
    fun formatProgress(progress: Float): String {
        return "${(progress * 100).toInt()}%"
    }
}
