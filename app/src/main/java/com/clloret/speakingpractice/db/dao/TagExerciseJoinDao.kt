package com.clloret.speakingpractice.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.domain.entities.TagExerciseJoin

@Dao
interface TagExerciseJoinDao {
    @Insert
    fun insert(tagExerciseJoin: TagExerciseJoin)

    @Query(
        """
               SELECT * FROM tags
               INNER JOIN tag_exercise_join
               ON tags.id=tag_exercise_join.tagId
               WHERE tag_exercise_join.exerciseId=:exerciseId
               """
    )
    fun getTagsForExercise(exerciseId: Int): Array<Exercise>

    @Query(
        """
               SELECT * FROM exercises
               INNER JOIN tag_exercise_join
               ON exercises.id=tag_exercise_join.exerciseId
               WHERE tag_exercise_join.tagId=:tagId
               """
    )
    fun getExercisesForTag(tagId: Int): Array<Tag>
}
