package com.clloret.speakingpractice.domain.exercise.list.sort

interface ExerciseSortable {
    val practicePhrase: String
    val correct: Int
    val incorrect: Int
    val successRate: Int
}
