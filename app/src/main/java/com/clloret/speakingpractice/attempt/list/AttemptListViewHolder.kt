package com.clloret.speakingpractice.attempt.list

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

class AttemptListViewHolder(private val binding: AttemptListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseAttempt) {
        binding.apply {
            attempt = item
            attemptTime = item.time.toString()
        }
    }
}
