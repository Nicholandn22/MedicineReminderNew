package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class KunjunganRepository(firestoreRepository: FirestoreRepository) {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("kunjungan")

    suspend fun addKunjungan(kunjungan: Kunjungan) {
        val docRef = if (kunjungan.idKunjungan.isBlank()) {
            collection.document() // auto generate ID
        } else {
            collection.document(kunjungan.idKunjungan)
        }

        val dataWithId = kunjungan.copy(idKunjungan = docRef.id) // pastikan ID tersimpan
        docRef.set(dataWithId).await()
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
