package com.clloret.speakingpractice.attempt.list

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListItemBinding
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import java.text.DateFormat

class AttemptListAdapter(
    private val viewModel: AttemptListViewModel,
    val listener: AttemptListListener
) :
    ListAdapter<AttemptWithExercise, AttemptListAdapter.AttemptListViewHolder>(
        AttemptListDiffCallback()
    ),
    OnClickAttemptHandler {

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
        holder.bind(item, viewModel, this)
    }

    override fun onClickMenu(view: View, attemptWithExercise: AttemptWithExercise) {
        PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_delete -> {
                        listener.onDeleteAttempt(attemptWithExercise.attempt.id); true
                    }
                    else -> false
                }
            }
            inflate(R.menu.menu_attempt_list_popup)
            gravity = Gravity.START
            show()
        }

    }

    public override fun getItem(position: Int): AttemptWithExercise {
        return super.getItem(position)
    }

    class AttemptListViewHolder(private val binding: AttemptListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: AttemptWithExercise,
            viewModel: AttemptListViewModel,
            onClickHandler: OnClickAttemptHandler
        ) {
            val df = DateFormat.getInstance()
            val formattedTime = df.format(item.attempt.time)

            binding.apply {
                entity = item
                attemptTime = formattedTime
                model = viewModel
                handlers = onClickHandler
            }
        }
    }

    interface AttemptListListener {
        fun onDeleteAttempt(attemptId: Int)
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

interface OnClickAttemptHandler {
    fun onClickMenu(view: View, attemptWithExercise: AttemptWithExercise)
}
