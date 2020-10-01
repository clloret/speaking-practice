package com.clloret.speakingpractice.utils.databinding.adapters

import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding

class TagChipChoice(private val tag: Tag) : ChipChoiceBinding {
    override val id: Int
        get() = tag.id
    override val displayName: String
        get() = tag.name
    override var selected = false
}
