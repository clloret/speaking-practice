package com.clloret.speakingpractice.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.clloret.speakingpractice.domain.resources.StringResourceProvider

class PreferenceValues(
    val context: Context,
    private val stringResourceProvider: StringResourceProvider
) {
    companion object {
        const val DEFAULT_EXERCISE_PER_ROUND = 10
    }

    private var preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun isAnalyticsEnabled(): Boolean {
        return preferences.getBoolean(stringResourceProvider.getPrefCollectStatistics(), true)
    }

    fun exercisesPerRound(): Int {
        return preferences.getInt(
            stringResourceProvider.getPrefExercisesPerRound(),
            DEFAULT_EXERCISE_PER_ROUND
        )
    }
}