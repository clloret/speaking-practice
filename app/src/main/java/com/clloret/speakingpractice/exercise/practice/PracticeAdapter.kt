package com.clloret.speakingpractice.exercise.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class PracticeAdapter(
    private val viewModel: PracticeViewModel
) :
    ListAdapter<ExerciseDetail, PracticeViewHolder>(ExerciseListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: PracticeItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.practice_item,
            parent, false
        )
        return PracticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PracticeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel)
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