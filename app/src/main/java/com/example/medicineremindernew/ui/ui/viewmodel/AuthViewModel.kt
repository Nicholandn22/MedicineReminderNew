//package com.example.medicineremindernew.ui.ui.viewmodel
//
//import android.app.Application
//import android.content.Context
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.medicineremindernew.ui.data.local.SessionManager
////import com.example.medicineremindernew.ui.data.model.User
////import com.example.medicineremindernew.ui.data.repository.UserRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class AuthViewModel(application: Application) : AndroidViewModel(application) {
//
//    // DAO dan Repository
//    private val userDao = ObatDatabase.getDatabase(application).userDao()
//    private val userRepository = UserRepository(userDao)
//
//    // Session
//    private val sessionManager = SessionManager(application.applicationContext)
//    val appContext: Context = application.applicationContext
//
//    // Email pengguna yang sedang login
//    private var _currentUserEmail: String? = null
//    val currentUserEmail: String?
//        get() = _currentUserEmail
//
//    // Login state
//    private val _loginSuccess = MutableStateFlow(false)
//    val loginSuccess = _loginSuccess.asStateFlow()
//
//    // Error state
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage = _errorMessage.asStateFlow()
//
//    // Fungsi register
//    fun register(email: String, password: String) {
//        viewModelScope.launch {
//            val existingUser = userDao.getUserByEmail(email)
//            if (existingUser != null) {
//                _errorMessage.value = "Email sudah digunakan"
//            } else {
//                userDao.insert(User(email = email, password = password))
//                sessionManager.saveEmail(email)
//                _currentUserEmail = email
//                _loginSuccess.value = true
//            }
//        }
//    }
//
//    // Fungsi login manual
//    fun login(email: String, password: String) {
//        viewModelScope.launch {
//            val user = userDao.login(email, password)
//            if (user != null) {
//                _currentUserEmail = email
//                sessionManager.saveEmail(email)
//                _loginSuccess.value = true
//            } else {
//                _errorMessage.value = "Email atau password salah"
//            }
//        }
//    }
//
//    // Fungsi login dari data session (biometrik)
//    fun loginWithBiometricSession(context: Context) {
//        val sessionEmail = SessionManager(context).getEmail()
//        if (!sessionEmail.isNullOrEmpty()) {
//            viewModelScope.launch {
//                val user = userDao.getUserByEmail(sessionEmail)
//                if (user != null) {
//                    _currentUserEmail = sessionEmail
//                    _loginSuccess.value = true
//                } else {
//                    _errorMessage.value = "Akun tidak ditemukan dalam database"
//                }
//            }
//        } else {
//            _errorMessage.value = "Tidak ada akun tersimpan untuk login biometrik"
//        }
//    }
//
//    // Fungsi untuk set login dari session saat pertama kali app dibuka (opsional)
//    fun setLoginSuccessFromSession(email: String) {
//        _currentUserEmail = email
//        _loginSuccess.value = true
//    }
//
//    // Fungsi logout
//    fun logout(context: Context) {
//        _loginSuccess.value = false
//        _currentUserEmail = null
//        // Jangan hapus session agar sidik jari masih bisa
//        // SessionManager(context).clearSession()
//    }
//
//
//    // Hapus pesan error
//    fun clearMessage() {
//        _errorMessage.value = null
//    }
//}
