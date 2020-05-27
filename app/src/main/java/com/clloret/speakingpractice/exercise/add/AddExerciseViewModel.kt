package com.clloret.speakingpractice.exercise.add

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.exercise.Exercise
import kotlinx.coroutines.runBlocking


class AddExerciseViewModel(
    private val repository: ExerciseRepository,
    private val exerciseId: Int
) : ViewModel() {

    var practicePhrase: ObservableField<String> = ObservableField()
    var translatedPhrase: ObservableField<String> = ObservableField()

    private val saveData = MutableLiveData<Boolean>()

    fun getSaveData(): LiveData<Boolean> = saveData

    init {
        repository.getExerciseById(exerciseId).apply {
            observeForever { value ->
                value?.let {
                    showData(it)
                }
            }
        }
    }

    private fun showData(exercise: Exercise) {
        practicePhrase.set(exercise.practicePhrase)
        translatedPhrase.set(exercise.translatedPhrase)
    }

    fun saveExercise() {
        if (canSave()) {
            val practicePhrase: String = practicePhrase.get()!!
            val translatedPhrase: String = translatedPhrase.get()!!
            val isNew = exerciseId == DEFAULT_ID
            val id = if (isNew) null else exerciseId
            val exercise = Exercise(id, practicePhrase, translatedPhrase)

            runBlocking {
                if (isNew) {
                    repository.insert(exercise)
                } else {
                    repository.update(exercise)
                }
            }
            saveData.postValue(true)
        } else {
            saveData.postValue(false)
        }
    }

    private fun canSave(): Boolean {
        val practicePhrase = this.practicePhrase.get()
        val translatedPhrase = this.translatedPhrase.get()

        return !practicePhrase.isNullOrBlank() && !translatedPhrase.isNullOrBlank()
    }

    companion object {
        const val DEFAULT_ID = -1
    }
}
