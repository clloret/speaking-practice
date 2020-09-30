package com.clloret.speakingpractice.utils.databinding.adapters

import com.clloret.speakingpractice.domain.entities.TagSelectedTuple
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding

class TagSelectedChipChoice(private val tagSelectedTuple: TagSelectedTuple) : ChipChoiceBinding {
    override val id: Int
        get() = tagSelectedTuple.id
    override val displayName: String
        get() = tagSelectedTuple.name
    override var selected: Boolean
        get() = tagSelectedTuple.selected
        set(value) {
            tagSelectedTuple.selected = value
        }
}
