package com.clloret.speakingpractice.exercise.practice

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class PracticeViewHolder(private val binding: PracticeItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: ExerciseWithDetails,
        viewModel: PracticeViewModel
    ) {
        binding.apply {
            exercise = item
            successRateColor = getSuccessRateColor(item.results.successRate)
            model = viewModel
        }
    }

    private fun getSuccessRateColor(successRate: Int) = when (successRate) {
        in 0..49 -> R.color.success_rate_failed
        in 50..79 -> R.color.success_rate_passed
        else -> R.color.success_rate_completed
    }
}