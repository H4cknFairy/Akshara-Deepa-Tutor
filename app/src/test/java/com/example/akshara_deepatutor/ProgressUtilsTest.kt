package com.example.akshara_deepatutor

import com.example.akshara_deepatutor.util.ProgressUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressUtilsTest {

    @Test
    fun calculateAverage_correctlyCalculatesAverage() {
        val scores = listOf(80, 60, 40)
        val result = ProgressUtils.calculateAverage(scores)
        assertEquals(60, result)
    }

    @Test
    fun calculateAverage_returnsZeroForEmptyList() {
        val scores = emptyList<Int>()
        val result = ProgressUtils.calculateAverage(scores)
        assertEquals(0, result)
    }

    @Test
    fun isWeakSubject_returnsTrueForScoreBelowThreshold() {
        assertTrue(ProgressUtils.isWeakSubject(59))
        assertTrue(ProgressUtils.isWeakSubject(40))
    }

    @Test
    fun isWeakSubject_returnsFalseForScoreAboveThreshold() {
        assertFalse(ProgressUtils.isWeakSubject(60))
        assertFalse(ProgressUtils.isWeakSubject(85))
    }

    @Test
    fun formatProgress_formatsCorrectly() {
        assertEquals("75%", ProgressUtils.formatProgress(0.75f))
        assertEquals("0%", ProgressUtils.formatProgress(0f))
        assertEquals("100%", ProgressUtils.formatProgress(1f))
    }
}
