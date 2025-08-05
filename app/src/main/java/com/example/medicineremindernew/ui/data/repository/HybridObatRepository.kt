package com.example.medicineremindernew.ui.data.repository

import android.util.Log
import com.example.medicineremindernew.ui.data.dao.LocalObatDao
import com.example.medicineremindernew.ui.data.entity.LocalObatEntity
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.network.NetworkUtils
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HybridObatRepository(
    private val obatRepository: ObatRepository,
    private val localDao: LocalObatDao,
    private val networkUtils: NetworkUtils
) {
    suspend fun addObat(obat: Obat): Boolean {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                obatRepository.addObat(obat) { /* callback optional */ }
                true
            } else {
                val localEntity = LocalObatEntity(
                    id = obat.id,
                    nama = obat.nama,
                    jenis = obat.jenis,
                    dosis = obat.dosis,
                    catatan = obat.catatan,
                    isSynced = false
                )
                localDao.insertObat(localEntity)
                true
            }
        } catch (e: Exception) {
            Log.e("HybridObatRepo", "Add failed: ${e.message}")
            false
        }
    }

    suspend fun updateObat(obat: Obat): Boolean {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                // ✅ Tunggu hasil dari suspending function
                val success = obatRepository.updateObat(obat)
                success
            } else {
                // ✅ Pastikan id tidak null
                val id = obat.id ?: return false

                val existing = localDao.getAllObat().find { it.id == id }
                if (existing == null) {
                    Log.e("HybridObatRepo", "Obat ID $id tidak ditemukan di lokal")
                    return false
                }

                val localEntity = LocalObatEntity(
                    id = id,
                    nama = obat.nama,
                    jenis = obat.jenis,
                    dosis = obat.dosis,
                    catatan = obat.catatan,
                    isSynced = false
                )
                localDao.updateObat(localEntity)
                true
            }
        } catch (e: Exception) {
            Log.e("HybridObatRepo", "Update failed: ${e.message}", e)
            false
        }
    }

    suspend fun deleteObat(id: String): Boolean {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                obatRepository.deleteObat(id)
            } else {
                localDao.deleteObat(id) // ✅ benar-benar hapus
            }
            true
        } catch (e: Exception) {
            Log.e("HybridObatRepo", "Delete failed: ${e.message}")
            false
        }
    }

    suspend fun getAllObat(): List<Obat> {
        return if (networkUtils.isNetworkAvailable()) {
            kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                var isResumed = false
                obatRepository.getAllObat { list ->
                    if (!isResumed) {
                        isResumed = true
                        continuation.resume(list) {}
                    }
                }
            }
        } else {
            getLocalObat()
        }
    }

    private suspend fun getLocalObat(): List<Obat> {
        return localDao.getAllObat().map {
            Obat(
                id = it.id,
                nama = it.nama,
                jenis = it.jenis,
                dosis = it.dosis,
                catatan = it.catatan
            )
        }
    }

    suspend fun syncPendingData() {
        if (!networkUtils.isNetworkAvailable()) return

        val unsynced = localDao.getUnsyncedObat()
        unsynced.forEach { entity ->
            val obat = Obat(
                id = entity.id,
                nama = entity.nama,
                jenis = entity.jenis,
                dosis = entity.dosis,
                catatan = entity.catatan
            )
            // Gunakan suspendCoroutine agar sinkronisasi menunggu callback selesai
            suspendCoroutine<Unit> { continuation ->
                obatRepository.addObat(obat) { success ->
                    if (success) {
                        // Karena ini sudah dalam coroutine (suspend function), kita bisa langsung panggil DAO tanpa GlobalScope
                        runBlocking {
                            localDao.markAsSynced(entity.id)
                            Log.d("HybridLansiaRepo", "Synced lansia: ${entity.id}")
                        }
                    }
                    continuation.resume(Unit)
                }
            }
        }
    }
}
