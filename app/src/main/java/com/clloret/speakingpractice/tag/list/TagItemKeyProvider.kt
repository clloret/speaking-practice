package com.clloret.speakingpractice.tag.list

import androidx.recyclerview.selection.ItemKeyProvider
import com.clloret.speakingpractice.domain.entities.Tag

class TagItemKeyProvider(private val list: List<Tag>) :
    ItemKeyProvider<Long>(
        SCOPE_CACHED
    ) {

    private val map = list.associateBy({ it.id.toLong() }, { list.indexOf(it) })

    override fun getKey(position: Int): Long {
        return list[position].id.toLong()
    }

    override fun getPosition(key: Long): Int {
        return map[key] ?: error("Key not found")
    }
}