package com.clloret.speakingpractice.attempt.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

class AttemptListAdapter :
    ListAdapter<ExerciseAttempt, AttemptListViewHolder>(AttemptListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttemptListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: AttemptListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.attempt_list_item,
            parent, false
        )
        return AttemptListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttemptListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class AttemptListDiffCallback : DiffUtil.ItemCallback<ExerciseAttempt>() {
    override fun areItemsTheSame(oldItem: ExerciseAttempt, newItem: ExerciseAttempt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExerciseAttempt, newItem: ExerciseAttempt): Boolean {
        return oldItem == newItem
    }
}