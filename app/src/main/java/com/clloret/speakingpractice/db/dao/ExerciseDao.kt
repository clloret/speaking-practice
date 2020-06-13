package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.*

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * FROM tags")
    fun getAllTags(): LiveData<List<Tag>>

    @Query("SELECT * FROM ExerciseDetail")
    fun getAllExercisesDetail(): LiveData<List<ExerciseDetail>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Int): LiveData<Exercise>

    @Query("SELECT SUM(result) AS correct, COUNT(*) - SUM(result) AS incorrect FROM exercises INNER JOIN exercise_attempts ON exercises.id = exercise_attempts.exercise_id GROUP BY exercise_id HAVING exercise_id=:exerciseId")
    fun getResultValues(exerciseId: Int): LiveData<ExerciseResultTuple>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM exercises WHERE id IN (:listIds)")
    suspend fun deleteList(listIds: List<Int>)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()

    @Query("DELETE FROM tag_exercise_join WHERE exercise_id=:exerciseId")
    suspend fun deleteAllFrom(exerciseId: Int)

    @Insert
    suspend fun insertAll(tagExerciseJoins: List<TagExerciseJoin>)

    @Transaction
    suspend fun insertOrUpdateExerciseAndTags(exercise: Exercise, tagsIds: List<Int>) {
        val id = insert(exercise)
        if (id == -1L) {
            update(exercise)
            deleteAllFrom(exercise.id)
        }

        val exerciseId: Int = if (id == -1L) exercise.id else id.toInt()
        val tagExerciseJoins = arrayListOf<TagExerciseJoin>()
        tagsIds.forEach {
            tagExerciseJoins.add(TagExerciseJoin(it, exerciseId))
        }
        insertAll(tagExerciseJoins)
    }

}