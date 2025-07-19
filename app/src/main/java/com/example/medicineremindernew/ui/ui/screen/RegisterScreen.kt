//package com.example.medicineremindernew.ui.ui.screen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModel
//
//@Composable
//fun RegisterScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//    val loginSuccess by viewModel.loginSuccess.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    if (loginSuccess) {
//        onLoginSuccess()
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                text = "Register",
//                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email") },
//                singleLine = true,
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                singleLine = true,
//                visualTransformation = PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = { viewModel.register(email, password) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//            ) {
//                Text("Register", fontSize = 16.sp)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            errorMessage?.let {
//                Text(it, color = Color.Red)
//            }
//        }
//    }
//}
