package com.clloret.speakingpractice.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.clloret.speakingpractice.R

class Dialogs(private val context: Context) {

    fun showConfirmation(
        @StringRes titleId: Int = R.string.title_confirmation,
        @StringRes messageId: Int,
        onCompletion: (Boolean) -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
            .apply {
                setTitle(titleId)
                setMessage(messageId)
                setPositiveButton("Yes") { _, _ -> onCompletion.invoke(true) }
                setNegativeButton("No") { _, _ -> onCompletion.invoke(false) }
            }

        builder.create().show()
    }

    companion object {
    }
}