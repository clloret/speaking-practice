package com.clloret.speakingpractice.exercise.list

import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import com.clloret.speakingpractice.utils.selection.LongItemDetails

class ExerciseListAdapter(
    comparator: Comparator<ExerciseSortable>,
    private val findNavController: NavController
) : RecyclerView.Adapter<ExerciseListAdapter.ViewHolder>(), OnClickExerciseHandler {

    private val adapterCallback = ExerciseListAdapterCallback(this, comparator)
    private val sortedList =
        SortedList<ExerciseWithDetails>(
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

    fun submitList(list: Collection<ExerciseWithDetails>) {
        sortedList.replaceAll(list)
    }

    fun setOrder(comparator: Comparator<ExerciseSortable>) {
        adapterCallback.comparator = comparator

        with(sortedList) {
            beginBatchedUpdates()

            val list = (0 until sortedList.size())
                .mapTo(ArrayList<ExerciseWithDetails>()) { get(it) }
            replaceAll(list)

            endBatchedUpdates()
        }
    }

    override fun onClick(exerciseDetail: ExerciseWithDetails) {
        val action = ExerciseListFragmentDirections.actionExerciseListFragmentToAttemptListFragment(
            filter = AttemptFilterByExercise(exerciseDetail.exercise.id)
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
