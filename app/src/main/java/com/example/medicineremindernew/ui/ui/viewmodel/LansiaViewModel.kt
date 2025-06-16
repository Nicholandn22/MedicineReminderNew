package com.example.medicineremindernew.ui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LansiaViewModel(private val repository: LansiaRepository) : ViewModel() {
//    val lansiaList: StateFlow<List<Lansia>> = repository.getAllLansia.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )



    val getAllLansia: Flow<List<Lansia>> = repository.getAllLansia



    fun insert(lansia: Lansia) = viewModelScope.launch {
        repository.insert(lansia)
    }

    fun delete(lansia: Lansia) = viewModelScope.launch {
        repository.delete(lansia)
    }

    fun update(lansia: Lansia) {
        viewModelScope.launch {
            repository.update(lansia)
        }
    }



    fun getLansiaById(id: Int): Flow<Lansia?> {
        return repository.getLansiaById(id)
    }


}
