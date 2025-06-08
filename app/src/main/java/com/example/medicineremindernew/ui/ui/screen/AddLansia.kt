package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun AddLansiaScreen(
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    var namaLansia by remember { mutableStateOf("") }
    var usia by remember { mutableStateOf("") }
    var golonganDarah by remember { mutableStateOf("A") }
    val golonganOptions = listOf("A", "B", "AB", "O")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(OrenMuda),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = "Tambah Lansia", color = Color.White, fontSize = 20.sp)
        }

        // Subheader "Data Lansia"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AbuMenu)
                .padding(16.dp)
        ) {
            Text("Data Lansia", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nama Lansia", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = namaLansia,
                    onValueChange = { namaLansia = it },
                    placeholder = { Text("Masukkan nama lansia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                Text("Usia", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = usia,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            usia = it
                        }
                    },
                    placeholder = { Text("Masukkan usia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                )

                Text("Golongan Darah", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = golonganOptions,
                    selectedOption = golonganDarah,
                    onOptionSelected = { golonganDarah = it }
                )
            }
        }

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Data Lansia berhasil disimpan")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Save")
            }

            Button(
                onClick = {
                    namaLansia = ""
                    usia = ""
                    golonganDarah = "A"
                    onCancelClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Clear")
            }
        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}