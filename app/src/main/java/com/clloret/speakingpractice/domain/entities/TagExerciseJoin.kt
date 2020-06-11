package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tag_exercise_join",
    primaryKeys = ["tag_id", "exercise_id"],
    foreignKeys = [ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tag_id")
    ), ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("exercise_id")
    )]
)
data class TagExerciseJoin(
    @ColumnInfo(name = "tag_id", index = true)
    val tagId: Int,
    @ColumnInfo(name = "exercise_id", index = true)
    val exerciseId: Int
)