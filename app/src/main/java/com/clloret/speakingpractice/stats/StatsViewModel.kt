package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.StatsRepository

class StatsViewModel(
    repository: StatsRepository
) : ViewModel() {

    val stats = repository.calculatedStats
}
