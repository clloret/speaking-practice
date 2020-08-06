package com.clloret.speakingpractice.domain.word

abstract class WordSortStrategy(private val orderType: OrderType) :
    Comparator<WordSortable> {
    fun getFirst(sortable1: WordSortable, sortable2: WordSortable): WordSortable {
        return if (orderType == OrderType.ASC) sortable1 else sortable2
    }

    fun getSecond(sortable1: WordSortable, sortable2: WordSortable): WordSortable {
        return if (orderType == OrderType.ASC) sortable2 else sortable1
    }

    enum class OrderType {
        DESC, ASC
    }
}
