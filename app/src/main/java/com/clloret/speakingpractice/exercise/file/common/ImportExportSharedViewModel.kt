package com.clloret.speakingpractice.exercise.file.common

import androidx.lifecycle.ViewModel

class ImportExportSharedViewModel : ViewModel() {
    var onShowImportHelp: (() -> Unit)? = null
    var onSelectFileToOpen: (() -> Unit)? = null
    var onShowExportHelp: (() -> Unit)? = null
    var onSelectFileToSave: (() -> Unit)? = null

    fun showImportHelp() {
        onShowImportHelp?.invoke()
    }

    fun selectFileToOpen() {
        onSelectFileToOpen?.invoke()
    }

    fun showExportHelp() {
        onShowExportHelp?.invoke()
    }

    fun selectFileToSave() {
        onSelectFileToSave?.invoke()
    }
}
