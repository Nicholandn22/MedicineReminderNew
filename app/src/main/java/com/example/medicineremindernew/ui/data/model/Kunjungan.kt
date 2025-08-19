package com.example.medicineremindernew.ui.data.model



import com.google.firebase.Timestamp
import java.util.Date


data class Kunjungan(
    val idKunjungan: String = "",            // Firestore ID
    val lansiaIds: List<String> = emptyList(),      // ID Lansia
    val waktu: String = "",         // "08:00"
    val tanggal: String = ""       // "2025-07-14"

)




