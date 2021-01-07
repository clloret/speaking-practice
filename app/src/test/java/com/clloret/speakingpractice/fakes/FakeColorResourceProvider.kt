package com.clloret.speakingpractice.fakes

import android.graphics.Color
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider

class FakeColorResourceProvider : ColorResourceProvider {
    override fun getExerciseCorrect(): Int {
        return Color.GREEN
    }

    override fun getExerciseIncorrect(): Int {
        return Color.RED
    }
}
