package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExerciseFilterByRandom(
    private val limit: Int,
    private val clock: Clock = Clock.systemDefaultZone()
) : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        val today = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now(clock))

        val ids = runBlocking {
            repository.getRandomExercisesIds(today, limit)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}
