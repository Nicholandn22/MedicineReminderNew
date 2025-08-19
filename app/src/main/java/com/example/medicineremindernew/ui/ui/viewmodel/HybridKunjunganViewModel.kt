// HybridKunjunganViewModel.kt
package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
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

    private val _kunjunganDetail = MutableStateFlow<Kunjungan?>(null)
    val kunjunganDetail: StateFlow<Kunjungan?> = _kunjunganDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadKunjungan()
    }

    fun loadKunjungan() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                val list = repository.getAllKunjungan()
                Log.d("HybridVM", "Dapat ${list.size} kunjungan dari repo")
                list.forEach { Log.d("HybridVM", "Data: $it") }
                _kunjunganList.value = list
            } catch (t: Throwable) {
                _error.value = t.message ?: "Unknown error"
                Log.e("HybridVM", "Error loadKunjungan", t)
            } finally {
                _loading.value = false
            }
        }
    }


    fun getKunjunganById(id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val list = repository.getAllKunjungan()
                _kunjunganDetail.value = list.find { it.idKunjungan == id }
            } catch (t: Throwable) {
                _error.value = t.message ?: "Load detail failed"
                _kunjunganDetail.value = null
            } finally {
                _loading.value = false
            }
        }
    }

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

    fun updateKunjungan(kunjungan: Kunjungan, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.updateKunjungan(kunjungan)
                if (success) {
                    loadKunjungan()
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
                    loadKunjungan()
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

    fun syncPendingData() {
        viewModelScope.launch {
            try {
                repository.syncPendingData()
                loadKunjungan()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
