package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.domain.entities.TagExerciseJoin
import com.clloret.speakingpractice.domain.entities.TagSelectedTuple

@Dao
interface TagExerciseJoinDao {
    @Insert
    suspend fun insert(tagExerciseJoin: TagExerciseJoin)

    @Insert
    suspend fun insertAllTagExerciseJoins(tagExerciseJoins: List<TagExerciseJoin>)

    @Query(
        """
               SELECT tags.tag_id, name FROM tags
               INNER JOIN tag_exercise_join
               ON tags.tag_id=tag_exercise_join.tag_id
               WHERE tag_exercise_join.exercise_id=:exerciseId
               """
    )
    fun getTagsForExercise(exerciseId: Int): LiveData<List<Tag>>

    @Query(
        """
              SELECT tags.tag_id, name, CASE WHEN exercise_id IS NULL THEN 0 ELSE 1 END AS selected 
              FROM tags 
              LEFT OUTER JOIN tag_exercise_join
              ON tags.tag_id=tag_exercise_join.tag_id
              AND tag_exercise_join.exercise_id=:exerciseId
               """
    )
    fun getSelectedTagsForExercise(exerciseId: Int): LiveData<List<TagSelectedTuple>>

    @Query(
        """
               SELECT exercises.exercise_id, practice_phrase, translated_phrase FROM exercises
               INNER JOIN tag_exercise_join
               ON exercises.exercise_id=tag_exercise_join.tag_id
               WHERE tag_exercise_join.tag_id=:tagId
               """
    )
    fun getExercisesForTag(tagId: Int): LiveData<List<Exercise>>

    @Query("DELETE FROM tag_exercise_join")
    suspend fun deleteAll()

    @Query("DELETE FROM tag_exercise_join WHERE exercise_id=:exerciseId")
    suspend fun deleteAllTagExerciseJoinsFrom(exerciseId: Int)

}
