package com.clloret.speakingpractice.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.clloret.speakingpractice.R
import com.google.android.material.chip.ChipGroup

object ChipFilterGroupBindingAdapter {

    @BindingAdapter("filterChips")
    @JvmStatic
    fun setChips(chipGroup: ChipGroup, chips: List<ChipChoiceBinding>?) {
        ChipGroupBindingAdapter.setChips(
            chipGroup,
            chips,
            R.style.Widget_AppTheme_Chip_Filter
        )
    }

    @InverseBindingAdapter(attribute = "filterChips")
    @JvmStatic
    fun getChips(
        chipGroup: ChipGroup
    ): List<ChipChoiceBinding> {
        return ChipGroupBindingAdapter.getChips(chipGroup)
    }

    @BindingAdapter("filterChipsAttrChanged")
    @JvmStatic
    fun setChipsChanged(chipGroup: ChipGroup, chipsAttrChanged: InverseBindingListener) {
        ChipGroupBindingAdapter.setChipsChanged(chipGroup, chipsAttrChanged)
    }
}
