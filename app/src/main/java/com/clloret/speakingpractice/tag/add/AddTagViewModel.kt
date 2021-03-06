package com.clloret.speakingpractice.tag.add

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.repository.TagRepository
import com.clloret.speakingpractice.domain.entities.Tag
import kotlinx.coroutines.launch

class AddTagViewModel(
    private val repository: TagRepository,
    private val tagId: Int
) : ViewModel() {

    var name: ObservableField<String> = ObservableField()

    private val saveData = MutableLiveData<Boolean>()

    enum class FormErrors {
        EMPTY_NAME,
    }

    val formErrors = ObservableArrayList<FormErrors>()

    init {
        viewModelScope.launch {
            repository.getTagById(tagId)?.let {
                showData(it)
            }
        }
    }

    private fun showData(tag: Tag) {
        name.set(tag.name)
    }

    private fun isFormValid(): Boolean {
        formErrors.clear()
        if (name.get().isNullOrBlank()) {
            formErrors.add(FormErrors.EMPTY_NAME)
        }
        return formErrors.isEmpty()
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

    fun saveData() {
        if (isFormValid()) {
            val name: String = name.get()?.trim() ?: return
            val isNew = tagId == DEFAULT_ID
            val id = if (isNew) 0 else tagId
            val tag = Tag(
                id,
                name
            )

            viewModelScope.launch {
                repository.insertOrUpdateTag(tag)
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
