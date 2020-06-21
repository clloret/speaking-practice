package com.clloret.speakingpractice.utils.databinding

import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import timber.log.Timber

class ChipEntryGroupBindingAdapter {
    companion object {

        @BindingAdapter("entryChips")
        @JvmStatic
        fun setChips(chipGroup: ChipGroup, chips: List<ChipEntryBinding>?) {
            Timber.d("setChips")

            chipGroup.removeAllViews()

            chips?.forEach {
                val chip = Chip(chipGroup.context).apply {
                    text = it.displayName
                }
                chipGroup.addView(chip)
            }
        }

    }
}