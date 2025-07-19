package com.example.medicineremindernew.ui.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Generic function untuk mengambil dokumen berdasarkan ID
     */
    suspend fun <T> getDocument(collection: String, documentId: String, clazz: Class<T>): T? {
        return try {
            val snapshot = db.collection(collection).document(documentId).get().await()
            if (snapshot.exists()) snapshot.toObject(clazz) else null
        } catch (e: Exception) {
            null
        }
    }
}
