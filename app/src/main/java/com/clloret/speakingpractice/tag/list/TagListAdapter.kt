package com.clloret.speakingpractice.tag.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.TagListItemBinding
import com.clloret.speakingpractice.domain.entities.Tag

class TagListAdapter :
    ListAdapter<Tag, TagListViewHolder>(TagListDiffCallback()) {

    var selectionTracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: TagListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.tag_list_item,
            parent, false
        )
        return TagListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagListViewHolder, position: Int) {
        val item = getItem(position)

        var isSelected = false
        selectionTracker?.let {
            if (it.isSelected(item.id.toLong())) {
                isSelected = true
            }
        }
        holder.bind(item, isSelected)
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