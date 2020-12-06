package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.StatsRepository
import java.time.Clock
import java.time.LocalDate

class StatsViewModel(
    repository: StatsRepository,
    clock: Clock = Clock.systemDefaultZone()
) : ViewModel() {

    val stats = repository.calculatedStats
    val dailyStats = repository.getDailyStatsFromDate(LocalDate.now(clock).minusDays(6))
}
