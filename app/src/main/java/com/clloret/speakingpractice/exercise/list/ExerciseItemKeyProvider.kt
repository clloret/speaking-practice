package com.clloret.speakingpractice.exercise.list

import androidx.recyclerview.selection.ItemKeyProvider
import com.clloret.speakingpractice.domain.entities.ExerciseDetail

class ExerciseItemKeyProvider(private val list: List<ExerciseDetail>) :
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