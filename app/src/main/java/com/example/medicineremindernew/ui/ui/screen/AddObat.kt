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
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.ui.theme.AbuMenu
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import kotlinx.coroutines.launch

@Composable
fun AddObatScreen(
    viewModel: ObatViewModel,
    onBackClick: () -> Unit = {},
) {
    var namaObat by remember { mutableStateOf("") }
    var jenisObat by remember { mutableStateOf("Tablet") }
    var satuanDosis by remember { mutableStateOf("mg") }
    var notes by remember { mutableStateOf("") }

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
            Text(text = "Tambah Obat", color = Color.White, fontSize = 20.sp)
        }

        // Subheader
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AbuMenu)
                .padding(16.dp)
        ) {
            Text("Data Obat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                Text("Nama Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = namaObat,
                    onValueChange = { namaObat = it },
                    placeholder = { Text("Masukkan nama obat") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                Text("Jenis Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("Tablet", "Sirup", "Salep"),
                    selectedOption = jenisObat,
                    onOptionSelected = { jenisObat = it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text("Satuan Dosis", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("mg", "ml", "IU"),
                    selectedOption = satuanDosis,
                    onOptionSelected = { satuanDosis = it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text("Notes (Opsional)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Catatan tambahan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 4.dp)
                )
            }
        }

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (namaObat.isBlank() || jenisObat.isBlank() || satuanDosis.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Data obat tidak boleh kosong!")
                        }
                    } else {
                        val newObat = Obat(
                            nama = namaObat,
                            jenis = jenisObat,
                            dosis = satuanDosis,
                            keterangan = notes
                        )
                        viewModel.insertObat(newObat)
                        scope.launch {
                            snackbarHostState.showSnackbar("Data Obat berhasil disimpan")
                        }
                        // Reset form
                        namaObat = ""
                        jenisObat = "Tablet"
                        satuanDosis = "mg"
                        notes = ""
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
                    namaObat = ""
                    jenisObat = "Tablet"
                    satuanDosis = "mg"
                    notes = ""
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
