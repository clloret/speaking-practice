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
            .setNeutralButton(R.string.action_cancel) { _, _ ->
                onCompletion.invoke(Button.NEUTRAL)
            }
            .setNegativeButton(R.string.action_no) { _, _ ->
                onCompletion.invoke(Button.NEGATIVE)
            }
            .setPositiveButton(R.string.action_yes) { _, _ ->
                onCompletion.invoke(Button.POSITIVE)
            }
            .show()
    }

    fun showConfirmation(
        @StringRes titleId: Int = R.string.title_confirmation,
        @StringRes messageId: Int,
        onCompletion: (Button) -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(titleId)
            .setMessage(messageId)
            .setNegativeButton(R.string.action_no) { _, _ ->
                onCompletion.invoke(Button.NEGATIVE)
            }
            .setPositiveButton(R.string.action_yes) { _, _ ->
                onCompletion.invoke(Button.POSITIVE)
            }
            .show()
    }

}