package com.clloret.speakingpractice.fakes

import com.clloret.speakingpractice.domain.PreferenceValues

class FakePreferenceValues : PreferenceValues {
    override fun isAnalyticsEnabled(): Boolean {
        return false
    }

    override fun exercisesPerRound(): Int {
        return 10
    }

    override fun dailyGoal(): Int {
        return 2
    }

    override fun isSoundEnabled(): Boolean {
        return false
    }

    override fun isSpeakPhraseEnabled(): Boolean {
        return false
    }
}
