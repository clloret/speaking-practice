package com.clloret.speakingpractice.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ExerciseValidatorTest {

    @Test
    fun `when the sentence is correct it return true`() {
        val text = "What is your name"
        val practicePhrase = "What is your name"
        val result = ExerciseValidator.validatePhrase(text, practicePhrase)

        assertThat(result).isTrue()
    }

    @Test
    fun `when the sentence is not correct it return false`() {
        val text = "What is your name"
        val practicePhrase = "What do you do"
        val result = ExerciseValidator.validatePhrase(text, practicePhrase)

        assertThat(result).isFalse()
    }

    @Test
    fun `when the sentence is correct even if there are different special chars it returns true`() {
        val text = "I don't understand"
        val practicePhrase = "I don´t understand"
        val result = ExerciseValidator.validatePhrase(text, practicePhrase)

        assertThat(result).isTrue()
    }

}