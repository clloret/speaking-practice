package com.clloret.speakingpractice.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.clloret.speakingpractice.domain.resources.StringResourceProvider

class PreferenceValues(
    val context: Context,
    private val stringResourceProvider: StringResourceProvider
) {
    private var preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun isAnalyticsEnabled(): Boolean {
        return preferences.getBoolean(stringResourceProvider.getPrefCollectStatistics(), true)
    }
}