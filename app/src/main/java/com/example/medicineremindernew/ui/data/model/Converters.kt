package com.example.medicineremindernew.ui.data.model

import androidx.room.TypeConverter
import java.sql.Time
import java.util.Date
import java.sql.Date as SqlDate

class Converters {

    // java.util.Date (untuk Lansia)
    @TypeConverter
    fun fromUtilDate(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter
    fun toUtilDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    // java.sql.Date (untuk Reminder.tanggal)
    @TypeConverter
    fun fromSqlDate(date: SqlDate?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toSqlDate(millis: Long?): SqlDate? {
        return millis?.let { SqlDate(it) }
    }

    // java.sql.Time (untuk Reminder.waktu)
    @TypeConverter
    fun fromSqlTime(time: Time?): Long? {
        return time?.time
    }

    @TypeConverter
    fun toSqlTime(millis: Long?): Time? {
        return millis?.let { Time(it) }
    }
}
