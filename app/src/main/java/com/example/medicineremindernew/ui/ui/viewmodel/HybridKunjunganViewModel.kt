package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.example.medicineremindernew.ui.data.repository.HybridKunjunganRepository
import kotlinx.coroutines.flow.*
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

    private val _kunjunganDetail = MutableStateFlow<Kunjungan?>(null)
    val kunjunganDetail: StateFlow<Kunjungan?> = _kunjunganDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        observeKunjungan()
    }

    /**
     * Mulai collect Flow dari repository → UI auto-update saat Room berubah.
     */
    private fun observeKunjungan() {
        viewModelScope.launch {
            repository.getAllKunjunganFlow()
                .onStart { _loading.value = true }
                .catch { e ->
                    _error.value = e.message ?: "Error observing kunjungan"
                    Log.e("HybridVM", "observeKunjungan error", e)
                }
                .collect { list ->
                    Log.d("HybridVM", "Flow emit ${list.size} kunjungan")
                    _kunjunganList.value = list
                    _loading.value = false
                }
        }
    }

    fun getKunjunganById(id: String) {
        val found = _kunjunganList.value.find { it.idKunjungan == id }
        _kunjunganDetail.value = found
    }

    fun addKunjungan(kunjungan: Kunjungan, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.addKunjungan(kunjungan)
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Add failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateKunjungan(kunjungan: Kunjungan, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.updateKunjungan(kunjungan)
                if (success) _kunjunganDetail.value = kunjungan
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Update failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteKunjungan(id: String, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.deleteKunjungan(id)
                if (success) _kunjunganDetail.value = null
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Delete failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun syncPendingData() {
        viewModelScope.launch {
            try {
                repository.syncPendingData()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
