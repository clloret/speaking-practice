package com.clloret.speakingpractice.db.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.TagSelectedTuple


class ExerciseRepository(private val db: AppDatabase) {

    val allExercisesDetails: LiveData<List<ExerciseWithDetails>> =
        db.exerciseDao().getExercisesWithDetails()

    suspend fun getAllExercises(): List<ExerciseWithDetails> {
        return db.exerciseDao().getAllExercises()
    }

    suspend fun getExerciseById(id: Int): Exercise {
        return db.exerciseDao().getExerciseById(id)
    }

    fun getExercisesDetailsByIds(ids: List<Int>): LiveData<List<ExerciseWithDetails>> {
        val idsOrZero = if (ids.isEmpty()) listOf(0) else ids
        val params = Array(idsOrZero.size) { "?" }.joinToString(",")
        val orderIds = idsOrZero.joinToString(",") { id -> "exercise_id = $id DESC" }
        val query = SimpleSQLiteQuery(
            "SELECT * FROM exercises WHERE exercise_id IN ($params) ORDER BY $orderIds",
            idsOrZero.toTypedArray()
        )
        return db.exerciseDao().getExercisesWithDetailsByIds(query)
    }

    suspend fun getExercisesIdsByTag(tagId: Int): List<Int> {
        return db.exerciseDao().getExercisesIdsByTag(tagId)
    }

    suspend fun getRandomExercisesIds(day: String, limit: Int): List<Int> {
        return db.exerciseDao().getRandomExercisesIds(day, limit)
    }

    suspend fun getMostFailedExercisesIds(successFactor: Double, minAttempts: Int): List<Int> {
        return db.exerciseDao().getMostFailedExercisesIds(successFactor, minAttempts)
    }

    suspend fun getLessPracticedExercisesIds(limit: Int): List<Int> {
        return db.exerciseDao().getLessPracticedExercisesIds(limit)
    }

    suspend fun getWordExercisesIds(word: String): List<Int> {
        return db.exerciseDao().getWordExercisesIds("% $word %")
    }

    suspend fun getSelectedTagsForExercise(exerciseId: Int): List<TagSelectedTuple> {
        return db.tagExerciseJoinDao().getSelectedTagsForExercise(exerciseId)
    }

    suspend fun deleteExerciseList(listIds: List<Int>) {
        db.exerciseDao().deleteList(listIds)
    }

    suspend fun insertOrUpdateExerciseAndTags(exercise: Exercise, tagsIds: List<Int>) {
        db.exerciseDao().insertOrUpdateExerciseAndTags(exercise, tagsIds, db.tagExerciseJoinDao())
    }

    suspend fun insertExerciseAndTags(exercise: Exercise, tagNames: List<String>) {
        db.exerciseDao()
            .insertExerciseAndTags(exercise, tagNames, db.tagDao(), db.tagExerciseJoinDao())
    }

    suspend fun deleteAllExercises() {
        db.exerciseDao().deleteAll()
    }

}
