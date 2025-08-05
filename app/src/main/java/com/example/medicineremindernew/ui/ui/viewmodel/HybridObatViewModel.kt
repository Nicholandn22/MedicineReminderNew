package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.HybridObatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HybridObatViewModel(
    private val hybridRepository: HybridObatRepository
) : ViewModel() {

    private val _obatList = MutableStateFlow<List<Obat>>(emptyList())
    val obatList: StateFlow<List<Obat>> = _obatList

    private val _obatDetail = MutableStateFlow<Obat?>(null)
    val obatDetail: StateFlow<Obat?> = _obatDetail

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
}
