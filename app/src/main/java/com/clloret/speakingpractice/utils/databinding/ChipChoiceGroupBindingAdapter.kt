package com.clloret.speakingpractice.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.clloret.speakingpractice.R
import com.google.android.material.chip.ChipGroup

object ChipChoiceGroupBindingAdapter {

    @BindingAdapter("choiceChips")
    @JvmStatic
    fun setChips(chipGroup: ChipGroup, chips: List<ChipChoiceBinding>?) {
        ChipGroupBindingAdapter.setChips(
            chipGroup,
            chips,
            R.style.Widget_MaterialComponents_Chip_Choice
        )
    }

    @InverseBindingAdapter(attribute = "choiceChips")
    @JvmStatic
    fun getChips(
        chipGroup: ChipGroup
    ): List<ChipChoiceBinding> {
        return ChipGroupBindingAdapter.getChips(chipGroup)
    }

    @Suppress("UNUSED_PARAMETER")
    @BindingAdapter("choiceChipsAttrChanged")
    @JvmStatic
    fun setChipsChanged(chipGroup: ChipGroup, chipsAttrChanged: InverseBindingListener) {
        ChipGroupBindingAdapter.setChipsChanged(chipGroup, chipsAttrChanged)
    }
}
