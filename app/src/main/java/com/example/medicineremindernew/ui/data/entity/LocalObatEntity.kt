package com.example.medicineremindernew.ui.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "local_obat")
data class LocalObatEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nama: String,
    val jenis: String,
    val dosis: String,
    val catatan: String,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)