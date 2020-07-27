package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.*

class AppRepository(private val db: AppDatabase) {

    val allTags = db.tagDao().getAllTags()

    val allExercisesDetails: LiveData<List<ExerciseWithDetails>> =
        db.exerciseDao().getExercisesWithDetails()

    val statistics = db.statisticsDao().getStatistics()

    suspend fun getExerciseById(id: Int): Exercise? {
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

    suspend fun getMostFailedExercisesIds(successFactor: Double, minAttempts: Int): List<Int> {
        return db.exerciseDao().getMostFailedExercisesIds(successFactor, minAttempts)
    }

    suspend fun getLessPracticedExercisesIds(limit: Int): List<Int> {
        return db.exerciseDao().getLessPracticedExercisesIds(limit)
    }

    fun getTagById(id: Int): LiveData<Tag> {
        return db.tagDao().getTagById(id)
    }

    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<AttemptWithExercise>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(id)
    }

    suspend fun getSelectedTagsForExercise(exerciseId: Int): List<TagSelectedTuple> {
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
        db.exerciseDao().insertOrUpdateExerciseAndTags(exercise, tagsIds, db.tagExerciseJoinDao())
    }

    suspend fun insertOrUpdateTag(tag: Tag) {
        db.tagDao().insertOrUpdate(tag)
    }

    suspend fun insertExerciseAndTags(exercise: Exercise, tagNames: List<String>) {
        db.exerciseDao()
            .insertExerciseAndTags(exercise, tagNames, db.tagDao(), db.tagExerciseJoinDao())
    }

}