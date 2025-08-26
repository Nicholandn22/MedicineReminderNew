package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.repository.HybridLansiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HybridLansiaViewModel(
    private val hybridRepository: HybridLansiaRepository
) : ViewModel() {

    private val _lansiaList = MutableStateFlow<List<Lansia>>(emptyList())
    val lansiaList: StateFlow<List<Lansia>> = _lansiaList

    private val _lansiaDetail = MutableStateFlow<Lansia?>(null)
    val lansiaDetail: StateFlow<Lansia?> = _lansiaDetail

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadLansia()
    }

    fun loadLansia() {
        viewModelScope.launch {
            _lansiaList.value = hybridRepository.getAllLansia()
        }
    }

    fun addLansia(lansia: Lansia, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.addLansia(lansia)
            loadLansia()
            onResult(success)
        }
    }

    fun getLansiaById(id: String) {
        viewModelScope.launch {
            _lansiaDetail.value = hybridRepository.getAllLansia().find { it.id == id }
        }
    }

    fun updateLansia(lansia: Lansia, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.updateLansia(lansia)
            if (success) loadLansia()
            onResult(success)
        }
    }

    fun deleteLansia(id: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = hybridRepository.deleteLansia(id)
            if (success) loadLansia()
            onResult(success)
        }
    }

    private fun observeLansia() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val list = hybridRepository.getAllLansia()
                _lansiaList.value = list.distinctBy { it.id }
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
                observeLansia()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
