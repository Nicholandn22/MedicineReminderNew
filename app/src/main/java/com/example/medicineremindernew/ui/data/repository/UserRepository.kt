package com.example.medicineremindernew.ui.data.repository


import com.example.medicineremindernew.ui.data.local.UserDao
import com.example.medicineremindernew.ui.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}
