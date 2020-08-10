package com.clloret.speakingpractice.domain.word

interface WordSortable {
    val word: String
    val correct: Int
    val incorrect: Int
    val successRate: Int
}