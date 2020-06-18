package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    val id: Int = 0,
    @ColumnInfo(name = "practice_phrase") val practicePhrase: String,
    @ColumnInfo(name = "translated_phrase") val translatedPhrase: String
)