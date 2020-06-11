package com.clloret.speakingpractice.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tag_exercise_join",
    primaryKeys = ["tagId", "exerciseId"],
    foreignKeys = [ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tagId")
    ), ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("exerciseId")
    )]
)
data class TagExerciseJoin(
    val tagId: Int,
    val exerciseId: Int
)