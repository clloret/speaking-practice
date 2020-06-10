package com.clloret.speakingpractice.exercise.import_

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.utils.Dialogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportExercises(
    private val context: Context
) : CoroutineScope by MainScope() {

    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    private fun initRepository(): ExerciseRepository {
        val db = ExercisesDatabase.getDatabase(context, this)
        return ExerciseRepository(db)
    }

    var onCompletion: ((Int) -> Unit)? = null

    fun import(uri: Uri, deleteAll: Boolean = true, completion: ((Int) -> Unit)?) {
        val exercises = readTsvFile(uri)
        runBlocking {
            if (deleteAll) {
                repository.deleteAllExercises()
            }
            for (exercise in exercises) {
                repository.insertExercise(exercise)
            }
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

    fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Dialogs(context)
                    .showConfirmationWithCancel(messageId = R.string.msg_replace_previous_exercises) { result ->
                        if (result != Dialogs.Button.NEUTRAL) {
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

    private fun readTsvFile(uri: Uri): List<Exercise> {
        try {
            val exercises = ArrayList<Exercise>()
            val contentResolver = context.contentResolver

            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    // Read the header
                    reader.readLine()

                    var line: String? = reader.readLine()
                    while (line != null) {
                        val tokens = line.split(TSV_DELIMITER)
                        if (tokens.isNotEmpty()) {
                            val exercise =
                                Exercise(
                                    practicePhrase = tokens[PRACTICE_PHRASE_IDX],
                                    translatedPhrase = tokens[TRANSLATED_PHRASE_IDX]
                                )
                            exercises.add(exercise)
                        }

                        line = reader.readLine()
                    }
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
        private const val FILE_READ_REQUEST_CODE: Int = 0x01
        private const val TSV_DELIMITER = "\t"
    }
}

