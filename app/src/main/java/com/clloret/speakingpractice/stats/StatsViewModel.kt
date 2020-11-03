package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.StatsRepository

class StatsViewModel(
    repository: StatsRepository
) : ViewModel() {

    val stats = repository.stats
}
