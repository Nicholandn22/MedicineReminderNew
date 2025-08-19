package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class KunjunganRepository(firestoreRepository: FirestoreRepository) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("kunjungan")

    suspend fun addKunjungan(kunjungan: Kunjungan) {
        collection.document(kunjungan.idKunjungan).set(kunjungan).await()
    }

    suspend fun updateKunjungan(kunjungan: Kunjungan) {
        collection.document(kunjungan.idKunjungan).set(kunjungan).await()
    }

    suspend fun deleteKunjungan(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun getAllKunjungan(): List<Kunjungan> {
        val snapshot = collection.get().await()
        return snapshot.toObjects(Kunjungan::class.java)
    }
}
