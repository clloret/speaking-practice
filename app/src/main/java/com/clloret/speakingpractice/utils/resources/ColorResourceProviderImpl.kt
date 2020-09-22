package com.clloret.speakingpractice.utils.resources

import android.content.Context
import androidx.core.content.ContextCompat
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider

class ColorResourceProviderImpl(val context: Context) : ColorResourceProvider {
    override fun getExerciseCorrect(): Int {
        return ContextCompat.getColor(context, R.color.exercise_correct)
    }

    override fun getExerciseIncorrect(): Int {
        return ContextCompat.getColor(context, R.color.exercise_incorrect)
    }
}
