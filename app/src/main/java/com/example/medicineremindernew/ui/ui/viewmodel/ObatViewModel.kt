package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ObatViewModel(private val repository: ObatRepository) : ViewModel() {
    val allObat: Flow<List<Obat>> = repository.getAllObat

    fun insert(obat: Obat) {
        viewModelScope.launch {
            repository.insert(obat)
        }
    }

    fun insertObat(obat: Obat) {
        viewModelScope.launch {
            repository.insert(obat)
        }
    }
    fun delete(obat: Obat) = viewModelScope.launch {
        repository.delete(obat)
    }

}
