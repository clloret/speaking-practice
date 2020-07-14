package com.clloret.speakingpractice.statistics

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class StatisticsViewModel(
    repository: AppRepository
) : ViewModel() {

    val statistics = repository.statistics

}