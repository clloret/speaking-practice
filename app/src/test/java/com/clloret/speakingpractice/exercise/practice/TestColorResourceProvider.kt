package com.clloret.speakingpractice.exercise.practice

import android.graphics.Color
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider

class TestColorResourceProvider : ColorResourceProvider {
    override fun getExerciseCorrect(): Int {
        return Color.GREEN
    }

    override fun getExerciseIncorrect(): Int {
        return Color.RED
    }
}