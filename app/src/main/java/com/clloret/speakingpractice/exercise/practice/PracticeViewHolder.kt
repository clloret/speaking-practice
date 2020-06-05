package com.clloret.speakingpractice.exercise.practice

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.PracticeItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class PracticeViewHolder(private val binding: PracticeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: ExerciseDetail,
        viewModel: PracticeViewModel
    ) {
        binding.apply {
            exercise = item
            model = viewModel
        }
    }
}