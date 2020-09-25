package com.clloret.speakingpractice.tag.list

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.TagListItemBinding
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.utils.selection.LongItemDetails

class TagListAdapter(val listener: TagListListener) :
    ListAdapter<Tag, TagListAdapter.ViewHolder>(DiffCallback()), OnClickTagHandler {

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
        holder.bind(item, isSelected, this)
    }

    class ViewHolder(private val binding: TagListItemBinding) :
        RecyclerView.ViewHolder(binding.root), LongItemDetails {

        fun bind(
            item: Tag,
            isSelected: Boolean,
            onClickHandler: OnClickTagHandler
        ) {
            binding.apply {
                tag = item
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

    interface TagListListener {
        fun onEditTag(tagId: Int)
        fun onDeleteTag(tagId: Int)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem == newItem
        }
    }

    override fun onClickMenu(view: View, tag: Tag) {
        PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit -> {
                        listener.onEditTag(tag.id); true
                    }
                    R.id.action_delete -> {
                        listener.onDeleteTag(tag.id); true
                    }
                    else -> false
                }
            }
            inflate(R.menu.menu_tag_list_popup)
            gravity = Gravity.START
            show()
        }
    }
}

interface OnClickTagHandler {
    fun onClickMenu(view: View, tag: Tag)
}
