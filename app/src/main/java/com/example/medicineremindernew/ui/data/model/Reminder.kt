package com.example.medicineremindernew.ui.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val obatId: Int,
    val lansiaId: Int,
    val waktu: Time,
    val tanggal: Date,
    val pengulangan : String
)
