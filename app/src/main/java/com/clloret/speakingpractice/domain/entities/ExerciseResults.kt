package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import kotlin.math.roundToInt

@DatabaseView(
    viewName = "exercise_results",
    value = """
        SELECT 
            exercises.exercise_id,
            SUM(result) AS correct, 
            COUNT(*) - SUM(result) AS incorrect
        FROM 
            exercises
            LEFT OUTER JOIN exercise_attempts ON exercises.exercise_id = exercise_attempts.exercise_id 
            GROUP BY exercises.exercise_id"""
)
data class ExerciseResults(
    @ColumnInfo(name = "exercise_id") val id: Int,
    val correct: Int,
    val incorrect: Int
) {
    val successRate: Int
        get() {
            val count = (correct + incorrect).takeIf { it > 0 } ?: return 0

            return (correct * 100 / count.toDouble()).roundToInt()
        }
}