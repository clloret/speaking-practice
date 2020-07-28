package com.clloret.speakingpractice.exercise.practice

import androidx.core.text.toHtml
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class FormatCorrectWordsTest(
    private val practicePhrase: String,
    private val correctWords: List<Pair<String, Boolean>>,
    private val expected: String
) {
    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0} - {1}")
        fun params(): List<Array<Any>> {
            return arrayListOf(
                arrayOf(
                    "we can't change the date of the meeting",
                    listOf(
                        Pair("we", false),
                        Pair("can't", false),
                        Pair("change", true),
                        Pair("the", true),
                        Pair("date", true),
                        Pair("of", true),
                        Pair("the", true),
                        Pair("meeting", true)
                    ),
                    "<p dir=ltr><font color =\"#ff0000\">we </font><font color =\"#ff0000\">can't </font><font color =\"#00ff00\">change </font><font color =\"#00ff00\">the </font><font color =\"#00ff00\">date </font><font color =\"#00ff00\">of </font><font color =\"#00ff00\">the </font><font color =\"#00ff00\">meeting</font></p>"
                ),
                arrayOf(
                    "i'd like a glass of white wine please",
                    listOf(
                        Pair("i'd", true),
                        Pair("like", true),
                        Pair("a", true),
                        Pair("glass", false),
                        Pair("of", true),
                        Pair("white", true),
                        Pair("wine", true),
                        Pair("please", true)
                    ),
                    "<p dir=ltr><font color =\"#00ff00\">i'd </font><font color =\"#00ff00\">like </font><font color =\"#00ff00\">a </font><font color =\"#ff0000\">glass </font><font color =\"#00ff00\">of </font><font color =\"#00ff00\">white </font><font color =\"#00ff00\">wine </font><font color =\"#00ff00\">please</font></p>"
                )
            ).toList()
        }
    }

    private val colorResourceProvider = TestColorResourceProvider()

    @Test
    fun getFormattedPracticePhrase() {

        val sut = FormatCorrectWords(colorResourceProvider)

        val result = sut.getFormattedPracticePhrase(practicePhrase, correctWords, true)

        assertThat(result.toHtml().trim())
            .isEqualTo(expected)
    }

}