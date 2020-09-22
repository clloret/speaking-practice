package com.clloret.speakingpractice.domain.resources

interface ColorResourceProvider {
    fun getExerciseCorrect(): Int

    fun getExerciseIncorrect(): Int
}
