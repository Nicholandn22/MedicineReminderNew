package com.example.medicineremindernew.ui.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "obat_table")
data class Obat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nama: String,
    val jenis: String,
    val dosis: String,
    val keterangan: String
)
