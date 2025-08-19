package com.example.medicineremindernew.ui.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "local_kunjungan")
data class LocalKunjunganEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val lansiaIds: String, // simpan sebagai string CSV "id1,id2"
    val tanggal: String,
    val waktu: String,
    val isSynced: Boolean = false, // ✅ Status sinkronisasi
    val createdAt: Long = System.currentTimeMillis()
)
