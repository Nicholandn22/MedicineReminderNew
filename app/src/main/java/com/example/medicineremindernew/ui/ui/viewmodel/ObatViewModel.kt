package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ObatViewModel(private val repository: ObatRepository) : ViewModel() {

    // Ambil semua obat
    val allObat: Flow<List<Obat>> = repository.getAllObat

    // Tambah obat
    fun insertObat(obat: Obat) {
        viewModelScope.launch {
            repository.insert(obat)
        }
    }

    // Update obat
    fun updateObat(obat: Obat) {
        viewModelScope.launch {
            repository.update(obat)
        }
    }

    // Hapus obat
    fun deleteObat(obat: Obat) {
        viewModelScope.launch {
            repository.delete(obat)
        }
    }

    // Ambil obat berdasarkan ID
    fun getObatById(id: Int): Flow<Obat?> {
        return repository.getObatById(id)
    }


    // (Opsional) Jika masih digunakan di fitur lain
    fun getLansiaById(id: Int): Flow<Obat?> {
        return repository.getObatById(id)
    }
}
