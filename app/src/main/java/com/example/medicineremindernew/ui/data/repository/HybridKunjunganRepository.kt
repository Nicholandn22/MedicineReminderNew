package com.example.medicineremindernew.ui.data.repository

import android.content.Context
import android.util.Log
import com.example.medicineremindernew.ui.data.dao.LocalKunjunganDao
import com.example.medicineremindernew.ui.data.entity.LocalKunjunganEntity
import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.example.medicineremindernew.ui.data.network.NetworkUtils

class HybridKunjunganRepository(
    private val kunjunganRepository: KunjunganRepository, // Firestore layer
    private val localDao: LocalKunjunganDao,
    private val networkUtils: NetworkUtils,
    private val context: Context
) {

    suspend fun addKunjungan(kunjungan: Kunjungan): Boolean {
        return try {
            val fixed = if (kunjungan.idKunjungan.isEmpty()) {
                kunjungan.copy(idKunjungan = java.util.UUID.randomUUID().toString())
            } else kunjungan

            if (networkUtils.isNetworkAvailable()) {
                kunjunganRepository.addKunjungan(fixed)
                localDao.insertKunjungan(fixed.toLocalEntity(isSynced = true))
            } else {
                localDao.insertKunjungan(fixed.toLocalEntity(isSynced = false))
            }
            true
        } catch (e: Exception) {
            Log.e("HybridKunjunganRepo", "Add failed: ${e.message}")
            false
        }
    }

    suspend fun updateKunjungan(kunjungan: Kunjungan): Boolean {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                kunjunganRepository.updateKunjungan(kunjungan)
                localDao.updateKunjungan(kunjungan.toLocalEntity(isSynced = true))
            } else {
                localDao.updateKunjungan(kunjungan.toLocalEntity(isSynced = false))
            }
            true
        } catch (e: Exception) {
            Log.e("HybridKunjunganRepo", "Update failed: ${e.message}")
            false
        }
    }

    suspend fun deleteKunjungan(id: String): Boolean {
        return try {
            if (networkUtils.isNetworkAvailable()) {
                kunjunganRepository.deleteKunjungan(id)
                localDao.deleteKunjungan(id)
            } else {
                localDao.deleteKunjungan(id)
            }
            true
        } catch (e: Exception) {
            Log.e("HybridKunjunganRepo", "Delete failed: ${e.message}")
            false
        }
    }

    suspend fun getAllKunjungan(): List<Kunjungan> {
        return if (networkUtils.isNetworkAvailable()) {
            try {
                kunjunganRepository.getAllKunjungan()
            } catch (e: Exception) {
                Log.e("HybridKunjunganRepo", "Get remote failed: ${e.message}")
                getLocalKunjungan()
            }
        } else {
            getLocalKunjungan()
        }
    }

    private suspend fun getLocalKunjungan(): List<Kunjungan> {
        return localDao.getAllKunjungan().map { it.toDomainModel() }
    }

    suspend fun syncPendingData() {
        if (!networkUtils.isNetworkAvailable()) return

        val unsynced = localDao.getUnsyncedKunjungan()
        unsynced.forEach { entity ->
            val kunjungan = entity.toDomainModel()
            try {
                kunjunganRepository.addKunjungan(kunjungan)
                localDao.markAsSynced(entity.id)
            } catch (e: Exception) {
                Log.e("HybridKunjunganRepo", "Sync failed for ${entity.id}: ${e.message}")
            }
        }
    }

    // Mapping extensions
    private fun Kunjungan.toLocalEntity(isSynced: Boolean): LocalKunjunganEntity {
        return LocalKunjunganEntity(
            id = this.idKunjungan,
            lansiaIds = this.lansiaIds.joinToString(","),
            tanggal = this.tanggal,
            waktu = this.waktu,
            isSynced = isSynced
        )
    }

    private fun LocalKunjunganEntity.toDomainModel(): Kunjungan {
        return Kunjungan(
            idKunjungan = this.id,
            lansiaIds = this.lansiaIds.split(","),
            tanggal = this.tanggal,
            waktu = this.waktu
        )
    }
}
