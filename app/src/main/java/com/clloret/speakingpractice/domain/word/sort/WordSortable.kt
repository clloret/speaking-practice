package com.clloret.speakingpractice.domain.word.sort

interface WordSortable {
    val word: String
    val correct: Int
    val incorrect: Int
    val successRate: Int
}
