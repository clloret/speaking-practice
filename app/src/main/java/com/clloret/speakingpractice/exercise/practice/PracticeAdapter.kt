package com.clloret.speakingpractice.exercise.practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class PracticeAdapter(
    private val viewModel: PracticeViewModel
) :
    ListAdapter<ExerciseWithDetails, PracticeAdapter.ViewHolder>(ExerciseListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: PracticeItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.practice_item,
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, viewModel, position + 1, itemCount)
    }

    public override fun getItem(position: Int): ExerciseWithDetails {
        return super.getItem(position)
    }

    class ViewHolder(private val binding: PracticeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ExerciseWithDetails,
            viewModel: PracticeViewModel,
            position: Int,
            itemCount: Int
        ) {
            binding.apply {
                exercise = item
                successRateColor = getSuccessRateColor(item.results.successRate)
                model = viewModel
                exercisePosition = position
                totalExercises = itemCount
            }
        }

        private fun getSuccessRateColor(successRate: Int) = when (successRate) {
            in SUCCESS_RATE_FAILED_RANGE -> R.color.success_rate_failed
            in SUCCESS_RATE_SUCCESS_RANGE -> R.color.success_rate_passed
            else -> R.color.success_rate_completed
        }

        companion object {
            private val SUCCESS_RATE_FAILED_RANGE = 0..49
            private val SUCCESS_RATE_SUCCESS_RANGE = 50..79
        }
    }
}

class ExerciseListDiffCallback : DiffUtil.ItemCallback<ExerciseWithDetails>() {
    override fun areItemsTheSame(
        oldItem: ExerciseWithDetails,
        newItem: ExerciseWithDetails
    ): Boolean {
        return oldItem.exercise.id == newItem.exercise.id
    }

    override fun areContentsTheSame(
        oldItem: ExerciseWithDetails,
        newItem: ExerciseWithDetails
    ): Boolean {
        return oldItem == newItem
    }
}
