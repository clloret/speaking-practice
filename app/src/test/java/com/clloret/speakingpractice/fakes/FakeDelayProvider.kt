package com.clloret.speakingpractice.fakes

import com.clloret.speakingpractice.exercise.practice.DelayProvider

class FakeDelayProvider : DelayProvider {
    override suspend fun delay(timeMillis: Long) {
        // Do nothing
    }
}
