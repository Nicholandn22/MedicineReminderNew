package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.local.ObatDao
import com.example.medicineremindernew.ui.data.model.Obat
import kotlinx.coroutines.flow.Flow


class ObatRepository(private val obatDao: ObatDao) {
    val getAllObat: Flow<List<Obat>> = obatDao.getAllObat()

    suspend fun insert(obat: Obat) {
        obatDao.insert(obat)
    }

    suspend fun delete(obat: Obat) {
        obatDao.delete(obat)
    }
}
