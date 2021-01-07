package com.clloret.speakingpractice.fakes

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class FakeClock(private var instant: Instant) : Clock() {
    override fun getZone(): ZoneId {
        return ZoneId.systemDefault()
    }

    override fun withZone(zone: ZoneId?): Clock {
        return this
    }

    override fun instant(): Instant {
        return instant
    }

    fun addDays(amount: Long) {
        instant = instant.plus(amount, ChronoUnit.DAYS)
    }

    fun addSeconds(amount: Long) {
        instant = instant.plus(amount, ChronoUnit.SECONDS)
    }
}
