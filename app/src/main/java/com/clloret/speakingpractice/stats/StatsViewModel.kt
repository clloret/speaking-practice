package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class StatsViewModel(
    repository: AppRepository
) : ViewModel() {

    val stats = repository.stats

}