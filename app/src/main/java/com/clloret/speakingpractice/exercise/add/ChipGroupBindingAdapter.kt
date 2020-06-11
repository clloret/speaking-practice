package com.clloret.speakingpractice.exercise.add

import androidx.databinding.BindingAdapter
import com.clloret.speakingpractice.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


class ChipGroupBindingAdapter {
    companion object {
        @BindingAdapter("chips")
        @JvmStatic
        fun setEntries(chipGroup: ChipGroup, chips: List<ChipBindingEntry>?) {
            chipGroup.removeAllViews()

            chips?.forEach {
                val chip = Chip(chipGroup.context)
                val drawable = ChipDrawable.createFromAttributes(
                    chipGroup.context,
                    null,
                    0,
                    R.style.Widget_MaterialComponents_Chip_Choice
                )
                chip.setChipDrawable(drawable)
                chip.text = it.displayName
                chipGroup.addView(chip)
            }
        }
    }
}
