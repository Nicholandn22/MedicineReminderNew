package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Kunjungan
import com.example.medicineremindernew.ui.data.repository.HybridKunjunganRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
     * 🔹 Ambil data hanya dari Firestore via HybridRepo.getAllKunjunganOnce()
     */
    private fun observeKunjungan() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val list = repository.getAllKunjunganOnce()
                _kunjunganList.value = list.distinctBy { it.idKunjungan }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error fetching kunjungan"
                Log.e("HybridVM", "observeKunjungan error", e)
            } finally {
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
                if (success) observeKunjungan()
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
                if (success) {
                    observeKunjungan()
                    _kunjunganDetail.value = kunjungan
                }
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
                if (success) {
                    observeKunjungan()
                    _kunjunganDetail.value = null
                }
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Delete failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    /**
     * 🔹 Sync dulu (biar data offline masuk Firestore), lalu reload dari Firestore
     */
    fun syncPendingData() {
        viewModelScope.launch {
            try {
                repository.syncPendingData()
                observeKunjungan()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
