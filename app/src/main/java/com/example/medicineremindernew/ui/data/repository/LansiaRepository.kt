package com.example.medicineremindernew.ui.data.repository

import com.example.medicineremindernew.ui.data.local.LansiaDao
import com.example.medicineremindernew.ui.data.model.Lansia
import kotlinx.coroutines.flow.Flow

class LansiaRepository(private val dao: LansiaDao) {
    val getAllLansia: Flow<List<Lansia>> = dao.getAll()

    suspend fun insert(lansia: Lansia) = dao.insert(lansia)

    suspend fun delete(lansia: Lansia) = dao.delete(lansia)

    // 🆕 Tambahan:
    fun getLansiaById(id: Int): Flow<Lansia?> {
        return dao.getById(id)
    }

    suspend fun update(lansia: Lansia) = dao.update(lansia)
}
