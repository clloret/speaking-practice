package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import kotlin.math.roundToInt

@DatabaseView(
    viewName = "exercise_results",
    value = """
        SELECT 
            exercises.exercise_id,
            CAST(TOTAL(result) AS INT) AS correct, 
            COUNT(exercise_attempts.result) - CAST(TOTAL(result) AS INT) AS incorrect
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
    val count: Int
        get() {
            return correct + incorrect
        }
    val successRate: Int
        get() {
            return if (count > 0) (correct * 100 / count.toDouble()).roundToInt() else 0
        }
}
