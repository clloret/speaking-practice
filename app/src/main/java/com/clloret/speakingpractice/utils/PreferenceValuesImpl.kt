package com.clloret.speakingpractice.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.resources.StringResourceProvider

class PreferenceValuesImpl(
    val context: Context,
    private val stringResourceProvider: StringResourceProvider
) : PreferenceValues {
    companion object {
        const val DEFAULT_EXERCISE_PER_ROUND = 10
        const val DEFAULT_DAILY_GOAL = 10
    }

    private var preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun isAnalyticsEnabled(): Boolean {
        return preferences.getBoolean(stringResourceProvider.getPrefCollectStatistics(), true)
    }

    override fun exercisesPerRound(): Int {
        return preferences.getInt(
            stringResourceProvider.getPrefExercisesPerRound(),
            DEFAULT_EXERCISE_PER_ROUND
        )
    }

    override fun dailyGoal(): Int {
        return preferences.getInt(
            stringResourceProvider.getPrefDailyGoal(),
            DEFAULT_DAILY_GOAL
        )
    }

    override fun isSoundEnabled(): Boolean {
        return preferences.getBoolean(
            stringResourceProvider.getPrefEnableSound(),
            true
        )
    }

    override fun isSpeakPhraseEnabled(): Boolean {
        return preferences.getBoolean(
            stringResourceProvider.getPrefEnableSpeakPhrase(),
            true
        )
    }

    override fun isMoveToNextExerciseEnabled(): Boolean {
        return preferences.getBoolean(
            stringResourceProvider.getPrefEnableMoveToNextExercise(),
            true
        )
    }

}
