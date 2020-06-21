package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseResultTuple
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.TagExerciseJoin

@Dao
interface ExerciseDao {

    @Transaction
    @Query("SELECT * FROM exercises")
    fun getExercisesWithDetails(): LiveData<List<ExerciseWithDetails>>

    @Transaction
    @Query("SELECT * FROM exercises WHERE exercise_id IN (:ids)")
    fun getExercisesWithDetailsByIds(ids: List<Int>): LiveData<List<ExerciseWithDetails>>

    @Query(
        """
                SELECT 
                    exercises.exercise_id 
                FROM 
                    exercises 
                INNER JOIN tag_exercise_join 
                ON exercises.exercise_id = tag_exercise_join.exercise_id 
                WHERE tag_exercise_join.tag_id=:tagId
    """
    )
    suspend fun getExercisesIdsByTag(tagId: Int): List<Int>

    @Query("SELECT exercise_id FROM exercises ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomExercisesIds(limit: Int): List<Int>

    @Query("SELECT * FROM exercises WHERE exercise_id = :id")
    fun getExerciseById(id: Int): LiveData<Exercise>

    @Query("SELECT SUM(result) AS correct, COUNT(*) - SUM(result) AS incorrect FROM exercises INNER JOIN exercise_attempts ON exercises.exercise_id = exercise_attempts.exercise_id GROUP BY exercises.exercise_id HAVING exercises.exercise_id=:exerciseId")
    fun getResultValues(exerciseId: Int): LiveData<ExerciseResultTuple>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE exercise_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM exercises WHERE exercise_id IN (:listIds)")
    suspend fun deleteList(listIds: List<Int>)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()

    @Query("DELETE FROM tag_exercise_join WHERE exercise_id=:exerciseId")
    suspend fun deleteAllTagExerciseJoinsFrom(exerciseId: Int)

    @Insert
    suspend fun insertAllTagExerciseJoins(tagExerciseJoins: List<TagExerciseJoin>)

    @Transaction
    suspend fun insertOrUpdateExerciseAndTags(exercise: Exercise, tagsIds: List<Int>) {
        val id = insert(exercise)
        if (id == -1L) {
            update(exercise)
            deleteAllTagExerciseJoinsFrom(exercise.id)
        }

        val exerciseId: Int = if (id == -1L) exercise.id else id.toInt()
        val tagExerciseJoins = arrayListOf<TagExerciseJoin>()
        tagsIds.forEach {
            tagExerciseJoins.add(TagExerciseJoin(it, exerciseId))
        }
        insertAllTagExerciseJoins(tagExerciseJoins)
    }

}