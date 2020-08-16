package com.clloret.speakingpractice.exercise.import_

import androidx.lifecycle.ViewModel

class ImportExercisesSharedViewModel : ViewModel() {
    var onShowHelp: (() -> Unit)? = null
    var onSelectFile: (() -> Unit)? = null

    fun showHelp() {
        onShowHelp?.invoke()
    }

    fun selectFile() {
        onSelectFile?.invoke()
    }
}