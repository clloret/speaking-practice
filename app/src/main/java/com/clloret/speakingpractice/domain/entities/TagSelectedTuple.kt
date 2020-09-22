package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding

data class TagSelectedTuple(
    @ColumnInfo(name = "tag_id")
    override var id: Int,
    var name: String,
    override var selected: Boolean
) : ChipChoiceBinding {
    override val displayName: String
        get() = this.name
}
