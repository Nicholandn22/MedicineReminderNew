package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.repository.HybridLansiaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HybridLansiaViewModel(
    private val hybridRepository: HybridLansiaRepository
) : ViewModel() {

    private val _lansiaList = MutableStateFlow<List<Lansia>>(emptyList())
    val lansiaList: StateFlow<List<Lansia>> = _lansiaList

    private val _lansiaDetail = MutableStateFlow<Lansia?>(null)
    val lansiaDetail: StateFlow<Lansia?> = _lansiaDetail

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
}
