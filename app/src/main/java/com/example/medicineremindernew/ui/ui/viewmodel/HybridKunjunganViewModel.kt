// HybridKunjunganViewModel.kt
package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.example.medicineremindernew.ui.data.repository.HybridKunjunganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel hybrid: menggunakan HybridKunjunganRepository (local Room + Firestore).
 * Exposes state flows untuk daftar kunjungan, loading, dan error message.
 */
class HybridKunjunganViewModel(
    private val repository: HybridKunjunganRepository
) : ViewModel() {

    private val _kunjunganList = MutableStateFlow<List<Kunjungan>>(emptyList())
    val kunjunganList: StateFlow<List<Kunjungan>> = _kunjunganList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadKunjungan()
    }

    /** Load semua kunjungan (ambil dari repo — repo memilih antara remote atau local) */
    fun loadKunjungan() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val list = repository.getAllKunjungan()
                _kunjunganList.value = list
            } catch (t: Throwable) {
                _error.value = t.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * Tambah kunjungan. Memanggil callback(Boolean) setelah proses selesai.
     * Callback akan dipanggil di scope utama (UI).
     */
    fun addKunjungan(kunjungan: Kunjungan, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.addKunjungan(kunjungan)
                if (success) loadKunjungan()
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Add failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    /** Update kunjungan (by id inside model). */
    fun updateKunjungan(kunjungan: Kunjungan, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.updateKunjungan(kunjungan)
                if (success) loadKunjungan()
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Update failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun addKunjunganSuspend(kunjungan: Kunjungan): Boolean {
        return repository.addKunjungan(kunjungan)
    }

    /** Delete kunjungan by id. */
    fun deleteKunjungan(id: String, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.deleteKunjungan(id)
                if (success) loadKunjungan()
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Delete failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    /** Sinkronisasi pending lokal -> Firestore (pakai repository). */
    fun syncPendingData() {
        viewModelScope.launch {
            try {
                repository.syncPendingData()
                // refresh list setelah sync
                loadKunjungan()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
