package com.clloret.speakingpractice.domain.resources

interface StringResourceProvider {
    fun getPrefCollectStatistics(): String
    fun getPrefExercisesPerRound(): String
    fun getPrefEnableSound(): String
    fun getPrefEnableSpeakPhrase(): String
    fun getPrefDailyGoal(): String
}
