package com.clloret.speakingpractice.exercise.file.import_

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.utils.Dialogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class ImportExercises(
    private val context: Context
) : KoinComponent {

    private val repository: ExerciseRepository by inject()
    var onCompletion: ((Int) -> Unit)? = null

    suspend fun import(
        uri: Uri,
        deleteAll: Boolean = true,
        completion: ((Int) -> Unit)?
    ) {
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use {
                import(it, deleteAll, completion)
            }
        }
    }

    suspend fun import(
        inputStream: InputStream,
        deleteAll: Boolean = true,
        completion: ((Int) -> Unit)?
    ) {
        val exercises = readTsvFile(inputStream)
        if (deleteAll) {
            repository.deleteAllExercises()
        }
        for (exercise in exercises) {
            repository.insertExerciseAndTags(exercise.exercise, exercise.tagNames)
        }
        completion?.invoke(exercises.count())
    }

    fun performFileSearchFromFragment(fragment: Fragment): Boolean {

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/tab-separated-values"
        }

        fragment.startActivityForResult(intent, FILE_READ_REQUEST_CODE)

        return true
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        scope: CoroutineScope
    ) {
        if (requestCode == FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Dialogs(context)
                    .showConfirmationWithCancel(messageId = R.string.msg_replace_previous_exercises) { result ->
                        if (result != Dialogs.Button.NEUTRAL) {
                            scope.launch {
                                import(
                                    uri,
                                    result == Dialogs.Button.POSITIVE,
                                    onCompletion
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun readTsvFile(inputStream: InputStream): List<ExerciseWithTagNames> {
        try {
            val exercises = arrayListOf<ExerciseWithTagNames>()

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Read the header
                reader.readLine()

                var line: String? = reader.readLine()
                while (line != null) {
                    val tokens = line.split(TSV_DELIMITER)
                    if (tokens.isNotEmpty()) {
                        val import = tokens[IMPORT_IDX].toBoolean()
                        if (import) {
                            val tagsValue = tokens[TAGS_IDX]
                            val tagNames =
                                if (tagsValue.isBlank()) emptyList()
                                else tagsValue
                                    .split(",")
                                    .map { it.trim() }

                            val exercise =
                                Exercise(
                                    practicePhrase = tokens[PRACTICE_PHRASE_IDX],
                                    translatedPhrase = tokens[TRANSLATED_PHRASE_IDX]
                                )
                            exercises.add(ExerciseWithTagNames(exercise, tagNames))
                        }
                    }

                    line = reader.readLine()
                }
            }

            return exercises
        } catch (e: Exception) {
            Timber.e(e, "Reading TSV Error!")
        }
        return listOf()
    }

    companion object {
        private const val PRACTICE_PHRASE_IDX = 0
        private const val TRANSLATED_PHRASE_IDX = 1
        private const val IMPORT_IDX = 2
        private const val TAGS_IDX = 3
        private const val FILE_READ_REQUEST_CODE: Int = 0x01
        private const val TSV_DELIMITER = "\t"
    }

    data class ExerciseWithTagNames(val exercise: Exercise, val tagNames: List<String>)
}
