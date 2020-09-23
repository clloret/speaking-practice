package com.clloret.speakingpractice.domain

interface PreferenceValues {
    fun isAnalyticsEnabled(): Boolean
    fun exercisesPerRound(): Int
    fun isSoundEnabled(): Boolean
    fun isSpeakPhraseEnabled(): Boolean
}
