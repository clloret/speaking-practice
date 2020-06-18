package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "tag_exercise_join",
    primaryKeys = ["tag_id", "exercise_id"],
    foreignKeys = [ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("tag_id"),
        childColumns = arrayOf("tag_id"),
        onDelete = CASCADE
    ), ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("exercise_id"),
        childColumns = arrayOf("exercise_id"),
        onDelete = CASCADE
    )]
)
data class TagExerciseJoin(
    @ColumnInfo(name = "tag_id", index = true)
    val tagId: Int,
    @ColumnInfo(name = "exercise_id", index = true)
    val exerciseId: Int
)