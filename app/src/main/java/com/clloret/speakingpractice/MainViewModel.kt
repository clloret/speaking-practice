package com.clloret.speakingpractice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val message = MutableLiveData<String>()

    fun showMessage(message: String) {
        this.message.postValue(message)
    }
}