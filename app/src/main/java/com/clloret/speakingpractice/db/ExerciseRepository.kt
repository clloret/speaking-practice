package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.*

class ExerciseRepository(private val db: ExercisesDatabase) {

    val allExercises: LiveData<List<Exercise>> = db.exerciseDao().getAllExercises()

    val allTags = db.exerciseDao().getAllTags()

    val allExercisesDetails: LiveData<List<ExerciseDetail>> =
        db.exerciseDao().getAllExercisesDetail()

    fun getExerciseById(id: Int): LiveData<Exercise> {
        return db.exerciseDao().getExerciseById(id)
    }

    fun getExercisesDetailsByTag(tagId: Int): LiveData<List<ExerciseDetail>> {
        return db.exerciseDao().getExercisesDetailByTag(tagId)
    }

    fun getExercisesDetailsByIds(ids: List<Int>): LiveData<List<ExerciseDetail>> {
        return db.exerciseDao().getExercisesDetailsByIds(ids)
    }

    suspend fun getRandomExercisesIds(limit: Int): List<Int> {
        return db.exerciseDao().getRandomExercisesIds(limit)
    }

    fun getTagById(id: Int): LiveData<Tag> {
        return db.tagDao().getTagById(id)
    }

    fun getResultValues(exerciseId: Int): LiveData<ExerciseResultTuple> {
        return db.exerciseDao().getResultValues(exerciseId)
    }

    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<ExerciseAttempt>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(id)
    }

    fun getTagsForExercise(id: Int): LiveData<List<Tag>> {
        return db.tagExerciseJoinDao().getTagsForExercise(id)
    }

    fun getSelectedTagsForExercise(exerciseId: Int): LiveData<List<TagSelectedTuple>> {
        return db.tagExerciseJoinDao().getSelectedTagsForExercise(exerciseId)
    }

    suspend fun insertExercise(exercise: Exercise) {
        db.exerciseDao().insert(exercise)
    }

    suspend fun updateExercise(exercise: Exercise) {
        db.exerciseDao().update(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        db.exerciseDao().delete(exercise)
    }

    suspend fun deleteExerciseById(exerciseId: Int) {
        db.exerciseDao().deleteById(exerciseId)
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