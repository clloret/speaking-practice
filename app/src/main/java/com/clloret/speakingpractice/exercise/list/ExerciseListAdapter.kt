package com.clloret.speakingpractice.exercise.list

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListItemBinding
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByExercise
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterById
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import com.clloret.speakingpractice.utils.selection.LongItemDetails

class ExerciseListAdapter(
    comparator: Comparator<ExerciseSortable>,
    private val findNavController: NavController
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>(), OnClickExerciseHandler {

    private val adapterCallback = ExerciseListAdapterCallback(this, comparator)
    private val sortedList =
        SortedList(
            ExerciseWithDetails::class.java,
            adapterCallback
        )

    var selectionTracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = sortedList.get(position)
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
        val item = sortedList.get(position)

        var isSelected = false
        selectionTracker?.let {
            if (it.isSelected(item.exercise.id.toLong())) {
                isSelected = true
            }
        }
        holder.bind(item, isSelected, this)
    }

    override fun getItemCount() = sortedList.size()

    private fun getCurrentList() =
        (0 until sortedList.size())
            .mapTo(ArrayList<ExerciseWithDetails>()) { sortedList.get(it) }

    private fun updateList(list: Collection<ExerciseWithDetails>) {
        with(sortedList) {
            beginBatchedUpdates()
            replaceAll(list)
            endBatchedUpdates()
        }
    }

    override fun onClick(exerciseDetail: ExerciseWithDetails) {
        val action = ExerciseListFragmentDirections.actionExerciseListFragmentToPracticeActivity(
            ExerciseFilterById(exerciseDetail.exercise.id), "Practice An Exercise"
        )
        findNavController.navigate(action)
    }

    override fun onClickMenu(view: View, exerciseDetail: ExerciseWithDetails) {
        PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_show_attempts -> showExerciseAttempts(exerciseDetail)
                    else -> false
                }
            }
            inflate(R.menu.menu_exercise_list_popup)
            gravity = Gravity.START
            show()
        }
    }

    private fun showExerciseAttempts(exerciseDetail: ExerciseWithDetails): Boolean {
        val action = ExerciseListFragmentDirections
            .actionExerciseListFragmentToAttemptListFragment(
                AttemptFilterByExercise(exerciseDetail.exercise.id)
            )
        findNavController.navigate(action)

        return true
    }

    fun submitList(list: Collection<ExerciseWithDetails>) {
        updateList(list)
    }

    fun setOrder(comparator: Comparator<ExerciseSortable>) {
        adapterCallback.comparator = comparator
        updateList(getCurrentList())
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
    fun onClickMenu(view: View, exerciseDetail: ExerciseWithDetails)
}
