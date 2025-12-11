package com.example.medicineremindernew.ui.data.model

import com.google.firebase.Timestamp

data class Lansia(
    val id: String = "",              // ID bakal diisi oleh Firestore (document ID)
    val nama: String = "",
    val goldar: String = "",
    val gender: String = "",
    val lahir: Timestamp? = null,     // Timestamp biar kompatibel dengan Firestore
//    val nomorwali: Int = 0,
    val penyakit: String = "",
    val obatIds: List<String> = emptyList() // list id obat

)
