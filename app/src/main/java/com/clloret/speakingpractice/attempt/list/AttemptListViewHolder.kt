package com.clloret.speakingpractice.attempt.list

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import java.text.DateFormat

class AttemptListViewHolder(private val binding: AttemptListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseAttempt) {
        val df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT)
        val formattedTime = df.format(item.time)

        binding.apply {
            attempt = item
            attemptTime = formattedTime
        }
    }
}
