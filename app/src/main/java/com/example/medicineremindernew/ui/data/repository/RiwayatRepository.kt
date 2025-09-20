package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.model.Riwayat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RiwayatRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val riwayatCollection = firestore.collection("riwayat")

    suspend fun addRiwayat(riwayat: Riwayat) {
        riwayatCollection.document(riwayat.idRiwayat)
            .set(riwayat)
            .await()
    }

    suspend fun updateRiwayat(riwayat: Riwayat) {
        riwayatCollection.document(riwayat.idRiwayat)
            .set(riwayat)
            .await()
    }

    suspend fun deleteRiwayat(id: String) {
        riwayatCollection.document(id)
            .delete()
            .await()
    }

    suspend fun getAllRiwayat(): List<Riwayat> {
        val snapshot = riwayatCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Riwayat::class.java) }
    }

    suspend fun getRiwayatByLansia(lansiaId: String): List<Riwayat> {
        val snapshot = riwayatCollection
            .whereEqualTo("lansiaId", lansiaId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(Riwayat::class.java) }
    }
}
