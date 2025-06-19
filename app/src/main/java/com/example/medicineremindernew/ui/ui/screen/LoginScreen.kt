package com.example.medicineremindernew.ui.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    if (loginSuccess) {
        onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFC5007)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            elevation = cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selamat Datang",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.login(email, password)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tombol Sidik Jari
                Button(
                    onClick = {
                        if (activity != null) {
                            val biometricManager = BiometricManager.from(context)
                            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                                BiometricManager.BIOMETRIC_SUCCESS -> {
                                    showBiometricPrompt(
                                        activity = activity,
                                        context = context,
                                        onSuccess = {
                                            viewModel.loginWithBiometricSession(context)
                                        }
                                    )
                                }
                                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                                    Toast.makeText(context, "Perangkat tidak mendukung biometrik", Toast.LENGTH_SHORT).show()
                                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                                    Toast.makeText(context, "Belum ada sidik jari terdaftar", Toast.LENGTH_SHORT).show()
                                else ->
                                    Toast.makeText(context, "Biometrik tidak tersedia", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Gagal mendapatkan activity", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Login dengan Sidik Jari")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onNavigateToRegister) {
                    Text("Belum punya akun? Daftar di sini", color = Color.Black)
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    context: Context,
    onSuccess: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(context, "Autentikasi gagal: $errString", Toast.LENGTH_SHORT).show()
            }
        })

    val promptInfo = PromptInfo.Builder()
        .setTitle("Login dengan Sidik Jari")
        .setSubtitle("Gunakan sidik jari yang telah didaftarkan")
        .setNegativeButtonText("Batal")
        .build()

    biometricPrompt.authenticate(promptInfo)
}
