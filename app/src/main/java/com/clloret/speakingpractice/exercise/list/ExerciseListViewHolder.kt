package com.clloret.speakingpractice.exercise.list

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class ExerciseListViewHolder(private val binding: ExerciseListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseDetail, onClickHandlers: Handlers) {
        binding.apply {
            exercise = item
            handlers = onClickHandlers
        }
    }
}

interface Handlers {
    fun onClick(exerciseDetail: ExerciseDetail)
}