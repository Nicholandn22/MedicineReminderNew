package com.example.medicineremindernew.ui.ui.viewmodel

// ObatViewModelFactory.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicineremindernew.ui.data.repository.ObatRepository

class ObatViewModelFactory(private val repository: ObatRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObatViewModel::class.java)) {
            return ObatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
