package com.clloret.speakingpractice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.utils.lifecycle.Event

class MainViewModel : ViewModel() {
    private val _message: MutableLiveData<Event<String>> = MutableLiveData()
    val message: LiveData<Event<String>> get() = _message

    fun showMessage(message: String) {
        this._message.postValue(Event(message))
    }
}
