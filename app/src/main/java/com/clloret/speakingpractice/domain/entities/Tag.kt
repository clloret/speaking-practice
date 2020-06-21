package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.clloret.speakingpractice.utils.databinding.ChipEntryBinding

@Entity(
    tableName = "tags", indices = [Index(value = ["name"], unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    val id: Int = 0,
    val name: String
) : ChipEntryBinding {
    override val displayName: String
        get() = name
}