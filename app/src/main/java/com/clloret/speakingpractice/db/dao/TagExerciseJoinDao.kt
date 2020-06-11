package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.domain.entities.TagExerciseJoin

@Dao
interface TagExerciseJoinDao {
    @Insert
    suspend fun insert(tagExerciseJoin: TagExerciseJoin)

    @Query(
        """
               SELECT id, name FROM tags
               INNER JOIN tag_exercise_join
               ON tags.id=tag_exercise_join.tag_id
               WHERE tag_exercise_join.exercise_id=:exerciseId
               """
    )
    fun getTagsForExercise(exerciseId: Int): LiveData<List<Tag>>

    @Query(
        """
               SELECT id, practice_phrase, translated_phrase FROM exercises
               INNER JOIN tag_exercise_join
               ON exercises.id=tag_exercise_join.tag_id
               WHERE tag_exercise_join.tag_id=:tagId
               """
    )
    fun getExercisesForTag(tagId: Int): LiveData<List<Exercise>>

    @Query("DELETE FROM tag_exercise_join")
    suspend fun deleteAll()
}
