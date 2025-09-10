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

    // Enum untuk kriteria sorting
    enum class SortCriteria {
        NAME_ASC,     // A-Z
        NAME_DESC,    // Z-A
        DATE_ASC,     // Tanggal terlama
        DATE_DESC,    // Tanggal terbaru
        STOCK_ASC,    // Stok sedikit
        STOCK_DESC    // Stok banyak
    }

    // StateFlow untuk kriteria sorting saat ini
    private val _currentSortCriteria = MutableStateFlow(SortCriteria.NAME_ASC)
    val currentSortCriteria: StateFlow<SortCriteria> = _currentSortCriteria.asStateFlow()

    init {
        loadObat()
    }

    // Fungsi untuk mengurutkan obat berdasarkan kriteria
    private fun sortObat(obatList: List<Obat>, criteria: SortCriteria): List<Obat> {
        return when (criteria) {
            SortCriteria.NAME_ASC -> obatList.sortedBy { it.nama.lowercase() }
            SortCriteria.NAME_DESC -> obatList.sortedByDescending { it.nama.lowercase() }
            SortCriteria.DATE_ASC -> obatList.sortedBy { it.pertamaKonsumsi?.toDate() }
            SortCriteria.DATE_DESC -> obatList.sortedByDescending { it.pertamaKonsumsi?.toDate() }
            SortCriteria.STOCK_ASC -> obatList.sortedBy { it.stok ?: 0 }
            SortCriteria.STOCK_DESC -> obatList.sortedByDescending { it.stok ?: 0 }
        }
    }

    fun loadObat() {
        viewModelScope.launch {
            val rawList = hybridRepository.getAllObat()
            val sortedList = sortObat(rawList, _currentSortCriteria.value)
            _obatList.value = sortedList
        }
    }

    // Fungsi untuk mengubah kriteria sorting
    fun setSortCriteria(criteria: SortCriteria) {
        _currentSortCriteria.value = criteria
        // Refresh data dengan sorting baru
        refreshObatWithCurrentSort()
    }

    private fun refreshObatWithCurrentSort() {
        viewModelScope.launch {
            try {
                val rawList = hybridRepository.getAllObat()
                val sortedList = sortObat(rawList, _currentSortCriteria.value)
                _obatList.value = sortedList
            } catch (e: Exception) {
                _error.value = e.message ?: "Error refreshing sorted obat"
                Log.e("HybridObatViewModel", "Error refreshing sorted obat: ${e.message}")
            }
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
