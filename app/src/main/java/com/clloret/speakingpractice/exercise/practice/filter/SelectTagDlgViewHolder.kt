package com.clloret.speakingpractice.exercise.practice.filter

import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.databinding.SelectTagDlgItemBinding
import com.clloret.speakingpractice.domain.entities.Tag

class SelectTagDlgViewHolder(private val binding: SelectTagDlgItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: Tag,
        onClickHandlers: Handlers
    ) {
        binding.apply {
            tag = item
            handlers = onClickHandlers
        }
    }
}

interface Handlers {
    fun onClick(tag: Tag)
}