package com.clloret.speakingpractice.exercise.list

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.exercise.list.sort.ExerciseSortable

class ExerciseListAdapterCallback(
    adapter: RecyclerView.Adapter<*>,
    var comparator: Comparator<ExerciseSortable>
) :
    SortedListAdapterCallback<ExerciseWithDetails>(adapter) {

    override fun areItemsTheSame(
        item1: ExerciseWithDetails,
        item2: ExerciseWithDetails
    ): Boolean {
        return item1.exercise.id == item2.exercise.id
    }

    override fun compare(o1: ExerciseWithDetails, o2: ExerciseWithDetails): Int {
        return comparator.compare(o1, o2)
    }

    override fun areContentsTheSame(
        oldItem: ExerciseWithDetails,
        newItem: ExerciseWithDetails
    ): Boolean {
        return oldItem == newItem
    }
}
