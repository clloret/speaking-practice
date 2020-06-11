package com.clloret.speakingpractice.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.clloret.speakingpractice.exercise.add.ChipBindingEntry

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
) : ChipBindingEntry {

    override val displayName: String
        get() = this.name
}