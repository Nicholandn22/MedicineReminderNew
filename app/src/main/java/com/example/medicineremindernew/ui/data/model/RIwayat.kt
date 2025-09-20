package com.example.medicineremindernew.ui.data.model

import java.util.*
import com.google.firebase.Timestamp
import java.util.Date

data class Riwayat(
    val idRiwayat: String = "",     // Firestore ID
    val lansiaId: String = "",      // ID Lansia
    val obatId: String? = null,     // ID Obat (jika minum obat)
    val kunjunganId: String? = null,// ID Kunjungan (jika kunjungan RS)

    val jenis: String = "",         // "MINUM_OBAT" | "KUNJUNGAN_RS"
    val keterangan: String = "",    // Catatan opsional

    val tanggal: String = "",       // "2025-09-20"
    val waktu: String = ""          // "08:00"
)
