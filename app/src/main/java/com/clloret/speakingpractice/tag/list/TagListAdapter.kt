package com.clloret.speakingpractice.tag.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.TagListItemBinding
import com.clloret.speakingpractice.domain.entities.Tag

class TagListAdapter :
    ListAdapter<Tag, TagListAdapter.ViewHolder>(TagListDiffCallback()) {

    var selectionTracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: TagListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.tag_list_item,
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        var isSelected = false
        selectionTracker?.let {
            if (it.isSelected(item.id.toLong())) {
                isSelected = true
            }
        }
        holder.bind(item, isSelected)
    }

    inner class ViewHolder(private val binding: TagListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Tag,
            isSelected: Boolean
        ) {
            binding.apply {
                tag = item
                itemView.isActivated = isSelected
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }
    }

}

class TagListDiffCallback : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }
}