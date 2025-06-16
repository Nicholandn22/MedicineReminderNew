package com.example.medicineremindernew.ui.ui.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.model.User
import com.example.medicineremindernew.ui.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = ObatDatabase.getDatabase(application).userDao()
    private val userRepository = UserRepository(userDao)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                _errorMessage.value = "Email sudah digunakan"
            } else {
                userDao.insert(User(email = email, password = password))
                _loginSuccess.value = true
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userDao.login(email, password)
            if (user != null) {
                _loginSuccess.value = true
            } else {
                _errorMessage.value = "Email atau password salah"
            }
        }
    }

    fun clearMessage() {
        _errorMessage.value = null
    }
}
