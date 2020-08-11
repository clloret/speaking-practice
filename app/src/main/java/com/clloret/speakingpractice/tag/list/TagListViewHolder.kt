package com.clloret.speakingpractice.tag.list

import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.TagListItemBinding
import com.clloret.speakingpractice.domain.entities.Tag

class TagListViewHolder(private val binding: TagListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: Tag,
        isSelected: Boolean
    ) {
        binding.apply {
            tag = item
            itemView.isActivated = isSelected
        }
    }

    fun getItemDetails(): ItemDetails<Long> =
        object : ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long? = itemId
        }
}
