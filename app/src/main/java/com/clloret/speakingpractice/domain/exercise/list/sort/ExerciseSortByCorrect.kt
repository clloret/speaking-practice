package com.clloret.speakingpractice.domain.exercise.list.sort

class ExerciseSortByCorrect(orderType: OrderType) : ExerciseSortStrategy(orderType) {
    override fun compare(sortable1: ExerciseSortable, sortable2: ExerciseSortable): Int {
        val s1 = getFirst(sortable1, sortable2)
        val s2 = getSecond(sortable1, sortable2)
        return s1.correct.compareTo(s2.correct)
    }
}
