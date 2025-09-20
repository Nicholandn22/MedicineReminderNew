package com.example.medicineremindernew.ui.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicineremindernew.ui.data.local.LocalDatabase
import com.example.medicineremindernew.ui.data.network.NetworkUtils
import com.example.medicineremindernew.ui.data.repository.RiwayatRepository
import com.example.medicineremindernew.ui.data.repository.HybridRiwayatRepository

class HybridRiwayatViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HybridRiwayatViewModel::class.java)) {
            val dao = LocalDatabase.getDatabase(context).riwayatDao()
            val firestoreRepo = RiwayatRepository()
            val networkUtils = NetworkUtils(context)

            val repository = HybridRiwayatRepository(
                riwayatRepository = firestoreRepo,
                localDao = dao,
                networkUtils = networkUtils,
                context = context
            )
            return HybridRiwayatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
