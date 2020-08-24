package com.clloret.speakingpractice.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExerciseValidatorTest {

    @Test
    fun `when the sentence is correct it return true`() {
        val text = "What is your name"
        val practicePhrase = "What is your name"
        val result = ExerciseValidator(arrayListOf(text), practicePhrase).validatePhrase(text)

        assertThat(result).isTrue()
    }

    @Test
    fun `when the sentence is not correct it return false`() {
        val text = "What is your name"
        val practicePhrase = "What do you do"
        val result = ExerciseValidator(arrayListOf(text), practicePhrase).validatePhrase(text)

        assertThat(result).isFalse()
    }

    @Test
    fun `when the sentence is correct even if there are different special chars it returns true`() {
        val text = "I don't understand"
        val practicePhrase = "I don´t understand"
        val result = ExerciseValidator(arrayListOf(text), practicePhrase).validatePhrase(text)

        assertThat(result).isTrue()
    }

    @Test
    fun `when any sentence is correct it returns true and the correct sentence`() {
        val list = listOf(
            "heat's on the table",
            "hits on the table",
            "Heats on the table",
            "it's on the table",
            "it's on the table."
        )

        val practicePhrase = "It's on the table"
        val result = ExerciseValidator(list, practicePhrase).getValidPhrase()

        assertThat(result.first).isTrue()
        assertThat(result.second).isEqualTo("it's on the table")
    }

    @Test
    fun `when no sentence is correct it returns false and the most correct`() {
        val list = listOf(
            "I am see him as soon as I can't",
            "I am see him as soon as I can",
            "I'm see him as soon I can"
        )

        val practicePhrase = "I'll see him as soon as I can"
        val result = ExerciseValidator(list, practicePhrase).getValidPhrase()

        assertThat(result.first).isFalse()
        assertThat(result.second).isEqualTo("I am see him as soon as I can")
    }

}