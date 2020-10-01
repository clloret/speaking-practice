package com.clloret.speakingpractice.domain.exercise.list.sort

abstract class ExerciseSortStrategy(private val orderType: OrderType) :
    Comparator<ExerciseSortable> {
    fun getFirst(sortable1: ExerciseSortable, sortable2: ExerciseSortable): ExerciseSortable {
        return if (orderType == OrderType.ASC) sortable1 else sortable2
    }

    fun getSecond(sortable1: ExerciseSortable, sortable2: ExerciseSortable): ExerciseSortable {
        return if (orderType == OrderType.ASC) sortable2 else sortable1
    }

    enum class OrderType {
        DESC, ASC
    }
}
