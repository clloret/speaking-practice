package com.clloret.speakingpractice.exercise.add

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

class ErrorTextBindingAdapter {
    companion object {
        @BindingAdapter("app:errorText")
        @JvmStatic
        fun setErrorMessage(view: TextInputLayout, errorMessage: String) {
            view.error = errorMessage
        }
    }
}