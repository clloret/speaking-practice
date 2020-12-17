package com.clloret.speakingpractice.domain

interface PreferenceValues {
    fun isAnalyticsEnabled(): Boolean
    fun isSoundEnabled(): Boolean
    fun isSpeakPhraseEnabled(): Boolean
    fun exercisesPerRound(): Int
    fun dailyGoal(): Int
}
