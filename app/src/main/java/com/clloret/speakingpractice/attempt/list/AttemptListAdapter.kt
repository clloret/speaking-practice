package com.clloret.speakingpractice.attempt.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

class AttemptListAdapter(
    private val viewModel: AttemptListViewModel
) :
    ListAdapter<AttemptWithExercise, AttemptListViewHolder>(AttemptListDiffCallback()) {

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
        holder.bind(item, viewModel)
    }
}

class AttemptListDiffCallback : DiffUtil.ItemCallback<AttemptWithExercise>() {
    override fun areItemsTheSame(
        oldItem: AttemptWithExercise,
        newItem: AttemptWithExercise
    ): Boolean {
        return oldItem.attempt.id == newItem.attempt.id
    }

    override fun areContentsTheSame(
        oldItem: AttemptWithExercise,
        newItem: AttemptWithExercise
    ): Boolean {
        return oldItem == newItem
    }
}