package com.example.medicineremindernew.ui.data.model

import com.google.firebase.Timestamp

data class Lansia(
    val id: String = "",              // ID akan diisi oleh Firestore (document ID)
    val nama: String = "",
    val goldar: String = "",
    val gender: String = "",
    val lahir: Timestamp? = null,     // Gunakan Timestamp agar kompatibel dengan Firestore
//    val nomorwali: Int = 0,       // Gunakan String agar lebih fleksibel
    val penyakit: String = "",
    val obatIds: List<String> = emptyList() // ✅ tambahkan field ini

)
