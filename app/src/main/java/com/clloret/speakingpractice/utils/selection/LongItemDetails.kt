package com.clloret.speakingpractice.utils.selection

import androidx.recyclerview.selection.ItemDetailsLookup

interface LongItemDetails {
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>
}
