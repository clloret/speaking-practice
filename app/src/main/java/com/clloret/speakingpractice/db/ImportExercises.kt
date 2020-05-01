package com.clloret.speakingpractice.db

import android.content.ContentResolver
import android.net.Uri
import com.clloret.speakingpractice.exercise.Exercise
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportExercises(
    private val repository: ExerciseRepository,
    private val contentResolver: ContentResolver
) {

    fun import(uri: Uri, deleteAll: Boolean = true, completion: (Int) -> Unit) {
        val exercises = readCsvFile(uri, contentResolver)
        runBlocking {
            if (deleteAll) {
                repository.deleteAll()
            }
            for (exercise in exercises) {
                repository.insert(exercise)
            }
        }
        completion(exercises.count())
    }

    private fun readCsvFile(uri: Uri, contentResolver: ContentResolver): List<Exercise> {
        try {
            val exercises = ArrayList<Exercise>()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    // Read the header
                    reader.readLine()

                    var line: String? = reader.readLine()
                    while (line != null) {
                        val tokens = line.split(",")
                        if (tokens.isNotEmpty()) {
                            val exercise = Exercise(
                                id = null,
                                practicePhrase = tokens[CUSTOMER_NAME_IDX],
                                translatedPhrase = tokens[CUSTOMER_ADDRESS_IDX]
                            )
                            exercises.add(exercise)
                        }

                        line = reader.readLine()
                    }
                }
            }

            return exercises
        } catch (e: Exception) {
            Timber.e(e, "Reading CSV Error!")
        }
        return listOf()
    }

    companion object {
        private const val CUSTOMER_NAME_IDX = 0
        private const val CUSTOMER_ADDRESS_IDX = 1
    }
}

