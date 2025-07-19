package com.example.medicineremindernew.ui.data.model



import com.google.firebase.Timestamp
import java.util.Date


data class Reminder(
    val id: String = "",            // Firestore ID
    val lansiaId: String = "",      // ID Lansia
    val obatId: String = "",        // ID Obat
    val waktu: String = "",         // "08:00"
    val tanggal: String = "",       // "2025-07-14"
    val pengulangan: String = ""    // "Harian", "Mingguan"
)




