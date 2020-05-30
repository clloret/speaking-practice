package com.clloret.speakingpractice.exercise.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class ExerciseListAdapter :
    ListAdapter<ExerciseDetail, ExerciseListViewHolder>(ExerciseListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ExerciseListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.exercise_list_item,
            parent, false
        )
        return ExerciseListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ExerciseListDiffCallback : DiffUtil.ItemCallback<ExerciseDetail>() {
    override fun areItemsTheSame(oldItem: ExerciseDetail, newItem: ExerciseDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExerciseDetail, newItem: ExerciseDetail): Boolean {
        return oldItem == newItem
    }
}