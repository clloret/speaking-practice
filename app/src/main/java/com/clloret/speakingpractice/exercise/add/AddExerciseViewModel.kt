package com.clloret.speakingpractice.exercise.add

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding
import com.clloret.speakingpractice.utils.databinding.adapters.TagSelectedChipChoice
import kotlinx.coroutines.launch
import timber.log.Timber

class AddExerciseViewModel(
    private val repository: AppRepository,
    private val exerciseId: Int
) : ViewModel() {

    var practicePhrase: ObservableField<String> = ObservableField()
    var translatedPhrase: ObservableField<String> = ObservableField()
    var exerciseTags: ObservableField<List<ChipChoiceBinding>> = ObservableField()

    private val saveData = MutableLiveData<Boolean>()

    enum class FormErrors {
        EMPTY_PRACTICE_PHRASE,
        EMPTY_TRANSLATED_PHRASE
    }

    val formErrors = ObservableArrayList<FormErrors>()

    init {
        viewModelScope.launch {
            repository.getExerciseById(exerciseId)?.let {
                showData(it)
            }

            repository.getSelectedTagsForExercise(exerciseId).let { tags ->
                exerciseTags.set(tags.map { TagSelectedChipChoice(it) }.sortedBy { it.displayName })
            }
        }
    }

    private fun showData(exercise: Exercise) {
        practicePhrase.set(exercise.practicePhrase)
        translatedPhrase.set(exercise.translatedPhrase)
    }

    private fun isFormValid(): Boolean {
        formErrors.clear()
        if (practicePhrase.get().isNullOrBlank()) {
            formErrors.add(FormErrors.EMPTY_PRACTICE_PHRASE)
        }
        if (translatedPhrase.get().isNullOrBlank()) {
            formErrors.add(FormErrors.EMPTY_TRANSLATED_PHRASE)
        }
        return formErrors.isEmpty()
    }

    private fun getSelectedTagsIds(selectedTags: List<ChipChoiceBinding>): ArrayList<Int> {
        val selectedTagIds = arrayListOf<Int>()
        selectedTags.forEach {
            Timber.d("Tag: $it")
            if (it.selected) {
                selectedTagIds.add(it.id)
            }
        }
        return selectedTagIds
    }

    fun getErrorMessage(
        formError: FormErrors,
        errorMessage: String,
        errors: ObservableArrayList<FormErrors>
    ): String {
        return if (errors.contains(formError)) {
            errorMessage
        } else {
            EMPTY_STRING
        }
    }

    fun getSaveData(): LiveData<Boolean> = saveData

    fun saveExercise() {
        if (isFormValid()) {
            val practicePhrase: String = practicePhrase.get()?.trim() ?: return
            val translatedPhrase: String = translatedPhrase.get()?.trim() ?: return
            val selectedTagIds = getSelectedTagsIds(exerciseTags.get() ?: return)
            val isNew = exerciseId == DEFAULT_ID
            val id = if (isNew) 0 else exerciseId
            val exercise = Exercise(
                id,
                practicePhrase,
                translatedPhrase
            )

            viewModelScope.launch {
                repository.insertOrUpdateExerciseAndTags(exercise, selectedTagIds)
            }.invokeOnCompletion {
                saveData.postValue(true)
            }
        } else {
            saveData.postValue(false)
        }
    }

    companion object {
        const val DEFAULT_ID = -1
        const val EMPTY_STRING = ""
    }
}
