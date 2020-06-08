package com.clloret.speakingpractice.exercise.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class ExerciseListAdapter(private val findNavController: NavController) :
    ListAdapter<ExerciseDetail, ExerciseListViewHolder>(ExerciseListDiffCallback()), Handlers {

    var selectionTracker: SelectionTracker<Long>? = null

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

        var isSelected = false
        if (selectionTracker != null) {
            if (selectionTracker!!.isSelected(item.id.toLong())) {
                isSelected = true
            }
        }
        holder.bind(item, position, isSelected, this)
    }

    override fun onClick(exerciseDetail: ExerciseDetail) {
        val action = ExerciseListFragmentDirections.actionExerciseListFragmentToAttemptListFragment(
            exerciseId = exerciseDetail.id
        )
        findNavController.navigate(action)
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