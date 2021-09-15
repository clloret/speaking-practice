package com.clloret.speakingpractice.utils.resources

import android.content.Context
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.resources.StringResourceProvider

class StringResourceProviderImpl(val context: Context) : StringResourceProvider {
    override fun getPrefCollectStatistics(): String {
        return context.getString(R.string.pref_collect_statistics)
    }

    override fun getPrefExercisesPerRound(): String {
        return context.getString(R.string.pref_exercises_per_round)
    }

    override fun getPrefEnableSound(): String {
        return context.getString(R.string.pref_enable_sound)
    }

    override fun getPrefEnableSpeakPhrase(): String {
        return context.getString(R.string.pref_enable_speak_phrase)
    }

    override fun getPrefEnableMoveToNextExercise(): String {
        return context.getString(R.string.pref_enable_move_to_next_exercise)
    }

    override fun getPrefDailyGoal(): String {
        return context.getString(R.string.pref_daily_goal)
    }
}
