package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.Tag

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE tag_id = :id")
    fun getTagById(id: Int): LiveData<Tag>

    @Query("SELECT * FROM tags WHERE name = :name")
    fun getTagByName(name: String): Tag?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(tag: Tag)

    @Transaction
    suspend fun insertOrUpdate(tag: Tag) {
        val id = insert(tag)
        if (id == -1L) {
            update(tag)
        }
    }

    @Query("DELETE FROM tags")
    suspend fun deleteAll()

    @Query("DELETE FROM tags WHERE tag_id IN (:listIds)")
    suspend fun deleteList(listIds: List<Int>)

}