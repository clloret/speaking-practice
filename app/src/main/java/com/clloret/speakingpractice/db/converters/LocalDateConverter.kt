package com.clloret.speakingpractice.db.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class LocalDateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long): LocalDate {
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate): Long {
        return date.toEpochDay()
    }
}
