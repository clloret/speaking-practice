package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.utils.lifecycle.Event

class SharedViewModel : ViewModel() {
    private val _selected: MutableLiveData<Event<Tag>> = MutableLiveData()
    val selected: LiveData<Event<Tag>> get() = _selected

    fun select(item: Tag) {
        _selected.postValue(Event(item))
    }
}