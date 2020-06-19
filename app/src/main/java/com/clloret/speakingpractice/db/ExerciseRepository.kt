package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.*

class ExerciseRepository(private val db: ExercisesDatabase) {

    val allTags = db.exerciseDao().getAllTags()

    val allExercisesDetails: LiveData<List<ExerciseWithDetails>> =
        db.exerciseDao().getExercisesWithDetails()

    fun getExerciseById(id: Int): LiveData<Exercise> {
        return db.exerciseDao().getExerciseById(id)
    }

    fun getExercisesDetailsByIds(ids: List<Int>): LiveData<List<ExerciseWithDetails>> {
        return db.exerciseDao().getExercisesWithDetailsByIds(ids)
    }

    suspend fun getExercisesIdsByTag(tagId: Int): List<Int> {
        return db.exerciseDao().getExercisesIdsByTag(tagId)
    }

    suspend fun getRandomExercisesIds(limit: Int): List<Int> {
        return db.exerciseDao().getRandomExercisesIds(limit)
    }

    fun getTagById(id: Int): LiveData<Tag> {
        return db.tagDao().getTagById(id)
    }

    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<ExerciseAttempt>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(id)
    }

    fun getSelectedTagsForExercise(exerciseId: Int): LiveData<List<TagSelectedTuple>> {
        return db.tagExerciseJoinDao().getSelectedTagsForExercise(exerciseId)
    }

    suspend fun insertExercise(exercise: Exercise) {
        db.exerciseDao().insert(exercise)
    }

    suspend fun deleteExerciseList(listIds: List<Int>) {
        db.exerciseDao().deleteList(listIds)
    }

    suspend fun deleteTagList(listIds: List<Int>) {
        db.tagDao().deleteList(listIds)
    }

    suspend fun deleteAllExercises() {
        db.exerciseDao().deleteAll()
    }

    suspend fun insertAttempt(exerciseResult: ExerciseAttempt) {
        db.exerciseAttemptDao().insert(exerciseResult)
    }

    suspend fun insertOrUpdateExerciseAndTags(exercise: Exercise, tagsIds: List<Int>) {
        db.exerciseDao().insertOrUpdateExerciseAndTags(exercise, tagsIds)
    }

    suspend fun insertOrUpdateTag(tag: Tag) {
        db.tagDao().insertOrUpdate(tag)
    }

}