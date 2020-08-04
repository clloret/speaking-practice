package com.clloret.speakingpractice.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListItemBinding
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults

class WordListAdapter :
    ListAdapter<PracticeWordWithResults, WordListViewHolder>(WordListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: WordListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.word_list_item,
            parent, false
        )
        return WordListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class WordListDiffCallback : DiffUtil.ItemCallback<PracticeWordWithResults>() {
    override fun areItemsTheSame(
        oldItem: PracticeWordWithResults,
        newItem: PracticeWordWithResults
    ): Boolean {
        return oldItem.word == newItem.word
    }

    override fun areContentsTheSame(
        oldItem: PracticeWordWithResults,
        newItem: PracticeWordWithResults
    ): Boolean {
        return oldItem == newItem
    }
}