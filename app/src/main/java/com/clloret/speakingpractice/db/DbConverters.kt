package com.clloret.speakingpractice.db

import androidx.room.TypeConverter
import java.util.*

class DbConverters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }
}
