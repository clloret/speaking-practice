package com.clloret.speakingpractice.db.repository

import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.Tag

class TagRepository(private val db: AppDatabase) {
    val allTags = db.tagDao().getAllTags()

    suspend fun getTagById(id: Int): Tag? {
        return db.tagDao().getTagById(id)
    }

    suspend fun insertOrUpdateTag(tag: Tag) {
        db.tagDao().insertOrUpdate(tag)
    }

    suspend fun deleteTagList(listIds: List<Int>) {
        db.tagDao().deleteList(listIds)
    }

}
