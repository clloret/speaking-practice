package com.clloret.speakingpractice.domain

class ExerciseValidator {
    companion object {
        fun validatePhrase(text: String, practicePhrase: String): Boolean {
            val cleanText = text.filter { it.isLetterOrDigit() }
            val cleanPhrase = practicePhrase.filter { it.isLetterOrDigit() }
            return cleanPhrase.equals(cleanText, true)
        }
    }
}