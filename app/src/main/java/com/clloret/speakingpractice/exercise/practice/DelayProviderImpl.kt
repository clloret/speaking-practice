package com.clloret.speakingpractice.exercise.practice

import com.clloret.speakingpractice.domain.DelayProvider

class DelayProviderImpl : DelayProvider {
    override suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
