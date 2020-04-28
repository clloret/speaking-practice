package com.clloret.speakingpractice.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "practice_phrase") val practicePhrase: String,
    @ColumnInfo(name = "translated_phrase") val translatedPhrase: String
)