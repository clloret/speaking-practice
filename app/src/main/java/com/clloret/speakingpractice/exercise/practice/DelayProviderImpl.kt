package com.clloret.speakingpractice.exercise.practice

class DelayProviderImpl : DelayProvider {
    override suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
