package com.clloret.speakingpractice.domain.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExerciseWithDetails(
    @Embedded val exercise: Exercise,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "tag_id",
        associateBy = Junction(TagExerciseJoin::class)
    )
    val tags: List<Tag>,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "exercise_id"
    )
    val results: ExerciseResults
)
