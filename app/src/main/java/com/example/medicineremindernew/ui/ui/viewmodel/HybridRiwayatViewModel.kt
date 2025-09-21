package com.example.medicineremindernew.ui.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Riwayat
import com.example.medicineremindernew.ui.data.repository.HybridRiwayatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HybridRiwayatViewModel(
    private val repository: HybridRiwayatRepository
) : ViewModel() {

    private val _riwayatList = MutableStateFlow<List<Riwayat>>(emptyList())
    val riwayatList: StateFlow<List<Riwayat>> = _riwayatList.asStateFlow()

    private val _riwayatDetail = MutableStateFlow<Riwayat?>(null)
    val riwayatDetail: StateFlow<Riwayat?> = _riwayatDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    enum class SortCriteria {
        TIMESTAMP_ASC,     // Timestamp terlama (tanggal + waktu)
        TIMESTAMP_DESC,    // Timestamp terbaru (tanggal + waktu)
    }

    // StateFlow untuk kriteria sorting saat ini - DEFAULT ke TIMESTAMP_DESC (terbaru dulu)
    private val _currentSortCriteria = MutableStateFlow(SortCriteria.TIMESTAMP_DESC)
    val currentSortCriteria: StateFlow<SortCriteria> = _currentSortCriteria.asStateFlow()

    // Combined StateFlow untuk data yang sudah di-sort
    val sortedRiwayatList: StateFlow<List<Riwayat>> = combine(
        _riwayatList,
        _currentSortCriteria
    ) { riwayatList, sortCriteria ->
        sortRiwayat(riwayatList, sortCriteria)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        observeRiwayat()
    }

    // Fungsi untuk mengurutkan riwayat berdasarkan kriteria
    private fun sortRiwayat(riwayatList: List<Riwayat>, criteria: SortCriteria): List<Riwayat> {
        return when (criteria) {
            SortCriteria.TIMESTAMP_ASC -> {
                riwayatList.sortedBy { riwayat ->
                    getTimestampFromRiwayat(riwayat)
                }
            }
            SortCriteria.TIMESTAMP_DESC -> {
                riwayatList.sortedByDescending { riwayat ->
                    getTimestampFromRiwayat(riwayat)
                }
            }
        }
    }

    // Fungsi helper untuk mendapatkan timestamp dari riwayat
    private fun getTimestampFromRiwayat(riwayat: Riwayat): Long {
        return try {
            val tanggalString = riwayat.tanggal // Format "yyyy-MM-dd"
            val waktuString = riwayat.waktu     // Format "HH:mm"

            if (tanggalString.isNotEmpty() && waktuString.isNotEmpty()) {
                // Gabungkan tanggal dan waktu menjadi timestamp
                val dateTimeString = "$tanggalString $waktuString"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val dateTime = sdf.parse(dateTimeString)
                dateTime?.time ?: 0L
            } else if (tanggalString.isNotEmpty()) {
                // Jika hanya ada tanggal, gunakan tanggal saja (jam 00:00)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.parse(tanggalString)
                date?.time ?: 0L
            } else {
                // Fallback ke 0 jika tidak ada data
                0L
            }
        } catch (e: Exception) {
            Log.e("HybridRiwayatVM", "Error parsing timestamp for riwayat ${riwayat.idRiwayat}", e)
            0L // Fallback value
        }
    }

    private fun observeRiwayat() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val list = repository.getAllRiwayatOnce()
                // distinct by idRiwayat biar tidak duplikat
                _riwayatList.value = list.distinctBy { it.idRiwayat }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error fetching riwayat"
                Log.e("HybridVM", "observeRiwayat error", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun getRiwayatById(id: String) {
        val found = _riwayatList.value.find { it.idRiwayat == id }
        _riwayatDetail.value = found
    }

    fun addRiwayat(riwayat: Riwayat, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.addRiwayat(riwayat)
                if (success) observeRiwayat()
                callback(success)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Add failed"
                callback(false)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateRiwayat(riwayat: Riwayat, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.updateRiwayat(riwayat)
                if (success) {
                    observeRiwayat()
                    _riwayatDetail.value = riwayat
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

    fun deleteRiwayat(id: String, callback: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val success = repository.deleteRiwayat(id)
                if (success) {
                    observeRiwayat()
                    _riwayatDetail.value = null
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
                observeRiwayat()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Sync failed"
            }
        }
    }
}
