package com.clloret.speakingpractice.utils.databinding

import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.clloret.speakingpractice.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import timber.log.Timber

class ChipChoiceGroupBindingAdapter {

    companion object {
        private var bindingListener: InverseBindingListener? = null

        @BindingAdapter("choiceChips")
        @JvmStatic
        fun setChips(chipGroup: ChipGroup, chips: List<ChipChoiceBinding>?) {
            Timber.d("setChips")

            chipGroup.removeAllViews()

            chips?.forEach {
                val drawable = ChipDrawable.createFromAttributes(
                    chipGroup.context,
                    null,
                    0,
                    R.style.Widget_MaterialComponents_Chip_Choice
                )
                val chip = Chip(chipGroup.context).apply {
                    setChipDrawable(drawable)
                    text = it.displayName
                    isChecked = it.selected
                    tag = it
                    setOnCheckedChangeListener { button, b ->
                        Timber.d("Chip: ${button.text}")
                        bindingListener?.onChange()
                    }

                }
                chipGroup.addView(chip)
            }
        }

        @InverseBindingAdapter(attribute = "choiceChips")
        @JvmStatic
        fun getChips(
            chipGroup: ChipGroup
        ): List<ChipChoiceBinding> {

            Timber.d("getChips")

            val list: ArrayList<ChipChoiceBinding> = arrayListOf()
            chipGroup.children.map { it as Chip }.forEach {
                val item = it.tag as ChipChoiceBinding
                item.selected = it.isChecked
                list.add(item)
            }

            return list
        }

        @Suppress("UNUSED_PARAMETER")
        @BindingAdapter("choiceChipsAttrChanged")
        @JvmStatic
        fun setChipsChanged(chipGroup: ChipGroup, chipsAttrChanged: InverseBindingListener) {
            Timber.d("setChipsChanged")

            bindingListener = chipsAttrChanged
        }

    }
}
