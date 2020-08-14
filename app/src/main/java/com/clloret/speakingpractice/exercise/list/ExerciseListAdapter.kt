package com.clloret.speakingpractice.exercise.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.utils.selection.LongItemDetails

class ExerciseListAdapter(private val findNavController: NavController) :
    ListAdapter<ExerciseWithDetails, ExerciseListAdapter.ViewHolder>(
        ExerciseListDiffCallback()
    ), OnClickExerciseHandler {

    var selectionTracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.exercise.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ExerciseListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.exercise_list_item,
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        var isSelected = false
        selectionTracker?.let {
            if (it.isSelected(item.exercise.id.toLong())) {
                isSelected = true
            }
        }
        holder.bind(item, isSelected, this)
    }

    override fun onClick(exerciseDetail: ExerciseWithDetails) {
        val action = ExerciseListFragmentDirections.actionExerciseListFragmentToAttemptListFragment(
            exerciseId = exerciseDetail.exercise.id
        )
        findNavController.navigate(action)
    }

    class ViewHolder(private val binding: ExerciseListItemBinding) :
        RecyclerView.ViewHolder(binding.root), LongItemDetails {

        fun bind(
            item: ExerciseWithDetails,
            isSelected: Boolean,
            onClickHandler: OnClickExerciseHandler
        ) {
            binding.apply {
                exercise = item
                handlers = onClickHandler
                itemView.isActivated = isSelected
            }
        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }
}

interface OnClickExerciseHandler {
    fun onClick(exerciseDetail: ExerciseWithDetails)
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