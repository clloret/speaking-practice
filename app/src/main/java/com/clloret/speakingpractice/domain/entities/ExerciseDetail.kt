package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView(
    viewName = "exercise_detail",
    value = """
        SELECT 
            exercises.*,
            tag_exercise_join.tag_id,
            SUM(result) AS correct, 
            COUNT(*) - SUM(result) AS incorrect 
        FROM 
            exercises
            LEFT OUTER JOIN tag_exercise_join ON exercises.id = tag_exercise_join.exercise_id
            LEFT OUTER JOIN exercise_attempts ON exercises.id = exercise_attempts.exercise_id 
            GROUP BY exercises.id
            """
)
data class ExerciseDetail(
    val id: Int,
    @ColumnInfo(name = "practice_phrase") val practicePhrase: String,
    @ColumnInfo(name = "translated_phrase") val translatedPhrase: String,
    @ColumnInfo(name = "tag_id") val tagId: Int,
    val correct: Int,
    val incorrect: Int
)