package com.clloret.speakingpractice.domain.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AttemptWithExercise(
    @Embedded val attempt: ExerciseAttempt,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val exercise: Exercise
)
