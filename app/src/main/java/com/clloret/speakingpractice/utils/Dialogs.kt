package com.clloret.speakingpractice.utils

import android.content.Context
import androidx.annotation.StringRes
import com.clloret.speakingpractice.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Dialogs(private val context: Context) {

    enum class Button {
        NEUTRAL, NEGATIVE, POSITIVE
    }

    fun showConfirmationWithCancel(
        @StringRes titleId: Int = R.string.title_confirmation,
        @StringRes messageId: Int,
        onCompletion: (Button) -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titleId)
            .setMessage(messageId)
            .setNeutralButton(R.string.cancel) { _, _ ->
                onCompletion.invoke(Button.NEUTRAL)
            }
            .setNegativeButton(R.string.no) { _, _ ->
                onCompletion.invoke(Button.NEGATIVE)
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                onCompletion.invoke(Button.POSITIVE)
            }
            .show()
    }
}