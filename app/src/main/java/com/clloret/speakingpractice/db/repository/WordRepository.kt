package com.clloret.speakingpractice.db.repository

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults

class WordRepository(db: AppDatabase) {
    val allPracticeWords: LiveData<List<PracticeWordWithResults>> =
        db.practiceWordDao().getPracticeWordsWithResults()
}
