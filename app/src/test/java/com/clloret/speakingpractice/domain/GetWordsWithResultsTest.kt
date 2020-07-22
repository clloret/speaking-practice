package com.clloret.speakingpractice.domain

import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.Serializable

@RunWith(value = Parameterized::class)
class GetWordsWithResultsTest(
    private val recognizedPhrase: String,
    private val practicePhrase: String,
    private val expected: Array<Pair<String, Boolean>>
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0} - {1}")
        fun data(): List<Array<Serializable>> {
            return arrayListOf(
                arrayOf(
                    "weekend change the date of the meeting",
                    "we can't change the date of the meeting",
                    arrayOf(
                        Pair("we", false),
                        Pair("can't", false),
                        Pair("change", true),
                        Pair("the", true),
                        Pair("date", true),
                        Pair("of", true),
                        Pair("the", true),
                        Pair("meeting", true)
                    )
                ),
                arrayOf(
                    "i'd like a lot of white wine please",
                    "i'd like a glass of white wine please",
                    arrayOf(
                        Pair("i'd", true),
                        Pair("like", true),
                        Pair("a", true),
                        Pair("glass", false),
                        Pair("of", true),
                        Pair("white", true),
                        Pair("wine", true),
                        Pair("please", true)
                    )
                )
            ).toList()
        }
    }

    @Test
    fun `when get words return correct and incorrect words`() {
        val result =
            ExerciseValidator.getWordsWithResults(recognizedPhrase, practicePhrase)

        Truth.assertThat(result).containsExactlyElementsIn(expected)
    }
}