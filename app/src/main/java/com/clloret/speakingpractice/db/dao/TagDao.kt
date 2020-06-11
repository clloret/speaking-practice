package com.clloret.speakingpractice.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Tag

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag)

    @Query("DELETE FROM tags")
    suspend fun deleteAll()

}