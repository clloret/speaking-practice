package com.clloret.speakingpractice.tag.list

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.TagListItemBinding
import com.clloret.speakingpractice.domain.entities.Tag


class TagListViewHolder(private val binding: TagListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val itemDetails = TagItemDetails()

    fun bind(
        item: Tag,
        position: Int,
        isSelected: Boolean
    ) {
        binding.apply {
            tag = item

            itemDetails.pos = position
            itemDetails.identifier = item.id.toLong()

            itemView.isActivated = isSelected
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun getItemDetails(motionEvent: MotionEvent): ItemDetails<Long> {
        return itemDetails
    }
}

//interface Handlers {
//    fun onClick(details: TagItemDetails)
//}