package com.clloret.speakingpractice.exercise.file.export

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

class ExportExercises(private val context: Context) : KoinComponent {
    private val repository: ExerciseRepository by inject()
    var onCompletion: ((Int) -> Unit)? = null

    private suspend fun export(
        uri: Uri,
        completion: ((Int) -> Unit)?
    ) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use {
                val exercises = repository.getAllExercises()
                saveTsvFile(it, exercises)
                completion?.invoke(exercises.size)
            }
        }
    }

    fun performFileSaveFromFragment(fragment: Fragment): Boolean {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/tab-separated-values"
            putExtra(Intent.EXTRA_TITLE, "Speaking Practice Exercises.tsv")
        }

        fragment.startActivityForResult(intent, FILE_SAVE_REQUEST_CODE)

        return true
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        scope: CoroutineScope
    ) {
        if (requestCode == FILE_SAVE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                scope.launch {
                    export(
                        uri,
                        onCompletion
                    )
                }
            }
        }
    }

    private fun saveTsvFile(outputStream: OutputStream, exercises: List<ExerciseWithDetails>) {
        BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
            writer.write("PracticePhrase\tTranslatedPhrase\tTags")
            writer.newLine()
            exercises.forEach { (exercise, tags) ->
                val strTags = tags.joinToString { it.name }
                writer.write("${exercise.practicePhrase}\t${exercise.translatedPhrase}\t$strTags")
                writer.newLine()
            }
        }
    }

    companion object {
        private const val FILE_SAVE_REQUEST_CODE: Int = 0x02
    }

}
