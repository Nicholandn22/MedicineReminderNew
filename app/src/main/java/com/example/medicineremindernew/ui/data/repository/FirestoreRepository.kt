package com.example.medicineremindernew.ui.data.repository

import android.util.Log
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

    suspend fun addDocument(collection: String, data: Map<String, Any>): Boolean {
        return try {
            db.collection(collection).add(data).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error adding document to $collection", e)
            false
        }
    }

//    suspend fun getDocuments(collection: String): List<Map<String, Any>> {
//        return try {
//            val result = db.collection(collection).get().await()
//            result.documents.map { doc ->
//                doc.data?.plus("id" to doc.id) ?: emptyMap()
//            }
//        } catch (e: Exception) {
//            Log.e("FirestoreRepository", "Error getting documents from $collection", e)
//            emptyList()
//        }
//    }
}
