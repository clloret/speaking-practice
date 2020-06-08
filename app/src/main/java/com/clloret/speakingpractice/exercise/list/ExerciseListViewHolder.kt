package com.clloret.speakingpractice.exercise.list

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail


class ExerciseListViewHolder(private val binding: ExerciseListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val itemDetails = ExerciseItemDetails()

    fun bind(
        item: ExerciseDetail,
        position: Int,
        isSelected: Boolean,
        onClickHandlers: Handlers
    ) {
        binding.apply {
            exercise = item
            handlers = onClickHandlers

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

interface Handlers {
    fun onClick(exerciseDetail: ExerciseDetail)
}