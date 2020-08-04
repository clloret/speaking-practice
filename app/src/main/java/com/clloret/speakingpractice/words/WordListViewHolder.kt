package com.clloret.speakingpractice.words

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.WordListItemBinding
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults

class WordListViewHolder(private val binding: WordListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: PracticeWordWithResults
    ) {
        binding.apply {
            word = item
        }
    }
}
