package com.clloret.speakingpractice.domain

import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class CheckCorrectWordsTest(
    private val recognizedPhrase: String,
    private val practicePhrase: String,
    private val correctPositions: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0} - {1}")
        fun data(): Iterable<Array<String>> {
            return arrayListOf(
                arrayOf(
                    "weekend change the date of the meeting",
                    "we can't change the date of the meeting",
                    "2,3,4,5,6,7"
                ),
                arrayOf(
                    "i'd like a lot of white wine please",
                    "i'd like a glass of white wine please",
                    "0,1,2,4,5,6,7"
                )
            ).toList()
        }
    }

    @Test
    fun `when check words return correct words positions`() {
        val result = ExerciseValidator.checkCorrectWords(recognizedPhrase, practicePhrase)
        val correctPositionsString = result.joinToString(",")

        Truth.assertThat(correctPositionsString).isEqualTo(correctPositions)
    }

}