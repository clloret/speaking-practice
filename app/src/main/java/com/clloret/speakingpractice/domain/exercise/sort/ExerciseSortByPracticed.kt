package com.clloret.speakingpractice.domain.exercise.sort

class ExerciseSortByPracticed(orderType: OrderType) : ExerciseSortStrategy(orderType) {
    override fun compare(sortable1: ExerciseSortable, sortable2: ExerciseSortable): Int {
        val s1 = getFirst(sortable1, sortable2)
        val s2 = getSecond(sortable1, sortable2)
        return s1.totalPracticed.compareTo(s2.totalPracticed)
    }
}

private val ExerciseSortable.totalPracticed: Int
    get() = correct + incorrect