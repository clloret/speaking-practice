package com.clloret.speakingpractice.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

object KeyboardUtils {

    fun hideSoftKeyboard(activity: Activity) {
        val systemService = activity.getSystemService<InputMethodManager>()
        systemService?.hideSoftInputFromWindow(
            activity.window.decorView.applicationWindowToken, 0
        )
    }

}
