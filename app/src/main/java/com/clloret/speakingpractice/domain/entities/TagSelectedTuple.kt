package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo

data class TagSelectedTuple(
    @ColumnInfo(name = "tag_id")
    var id: Int,
    var name: String,
    var selected: Boolean
)
