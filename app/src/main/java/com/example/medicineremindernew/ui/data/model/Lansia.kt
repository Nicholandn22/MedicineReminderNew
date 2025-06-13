package com.example.medicineremindernew.ui.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "lansia_table")
data class Lansia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val goldar: String,
    val gender: String,
    val lahir: Date,
    val nomorwali: Int,
    val penyakit: String
)
