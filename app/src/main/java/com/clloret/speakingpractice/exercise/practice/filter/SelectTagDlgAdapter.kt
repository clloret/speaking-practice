package com.clloret.speakingpractice.exercise.practice.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.SelectTagDlgItemBinding
import com.clloret.speakingpractice.domain.entities.Tag

class SelectTagDlgAdapter(private val onSelectTag: ((Tag) -> Unit)) :
    ListAdapter<Tag, SelectTagDlgViewHolder>(SelectTagDlgDiffCallback()), Handlers {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTagDlgViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: SelectTagDlgItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.select_tag_dlg_item,
            parent, false
        )
        return SelectTagDlgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectTagDlgViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item, this)
    }

    override fun onClick(tag: Tag) {
        onSelectTag.invoke(tag)
    }

}

class SelectTagDlgDiffCallback : DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }
}