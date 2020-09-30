package com.clloret.speakingpractice.utils.databinding.adapters

import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.utils.databinding.ChipEntryBinding

class TagChipEntry(private val tag: Tag) : ChipEntryBinding {
    override val displayName: String
        get() = tag.name
}
