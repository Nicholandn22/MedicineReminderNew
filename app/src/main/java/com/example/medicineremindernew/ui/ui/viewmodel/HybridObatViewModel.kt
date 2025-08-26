package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.HybridObatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HybridObatViewModel(
    private val hybridRepository: HybridObatRepository
) : ViewModel() {

    private val _obatList = MutableStateFlow<List<Obat>>(emptyList())
    val obatList: StateFlow<List<Obat>> = _obatList

    private val _obatDetail = MutableStateFlow<Obat?>(null)
    val obatDetail: StateFlow<Obat?> = _obatDetail

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadObat()
    }

    fun loadObat() {
        viewModelScope.launch {
            _obatList.value = hybridRepository.getAllObat()
        }
    }

    fun addObat(obat: Obat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.addObat(obat)
            loadObat()
            onResult(success)
        }
    }

    fun getObatById(id: String) {
        viewModelScope.launch {
            _obatDetail.value = hybridRepository.getAllObat().find { it.id == id }
        }
    }

    fun updateObat(obat: Obat, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.updateObat(obat)
            if (success) loadObat()
            onResult(success)
        }
    }

    fun deleteObat(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.deleteObat(id)
            if (success) loadObat()
            onResult(success)
        }
    }

    private fun observeObat() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val list = hybridRepository.getAllObat()
                _obatList.value = list.distinctBy { it.id }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error fetching kunjungan"
                Log.e("HybridVM", "observeKunjungan error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun syncPendingData() {
        viewModelScope.launch {
            try {
                hybridRepository.syncPendingData()
                observeObat()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
