package com.clloret.speakingpractice.attempt.list

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import java.text.DateFormat

class AttemptListViewHolder(private val binding: AttemptListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: AttemptWithExercise,
        viewModel: AttemptListViewModel
    ) {
        val df = DateFormat.getInstance()
        val formattedTime = df.format(item.attempt.time)

        binding.apply {
            entity = item
            attemptTime = formattedTime
            model = viewModel
        }
    }
}
