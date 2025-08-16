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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.viewmodel.HybridObatViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddObatScreen(
    viewModel: HybridObatViewModel,
    onBackClick: () -> Unit = {}
) {
    var namaObat by remember { mutableStateOf("") }
    var jenisObat by remember { mutableStateOf("Tablet") }
    var satuanDosis by remember { mutableStateOf("mg") }
    var notes by remember { mutableStateOf("") }
    var pertamaKonsumsi by remember { mutableStateOf<Date?>(null) }

    val blueColor = BiruMuda.copy(alpha = 1.0f)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            pertamaKonsumsi = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ✅ Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(16.dp)
                .background(BiruAgakTua.copy(alpha = 1.0f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(
                    painter = painterResource(id = back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text("Tambah Obat", color = Color.White, fontSize = 20.sp)
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
                OutlinedTextField(
                    value = namaObat,
                    onValueChange = { namaObat = it },
                    placeholder = { Text("Nama Obat") },
                    label = { Text("Masukkan Nama Obat") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    singleLine = true
                )

                Text("Jenis Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("Tablet", "Sirup", "Salep", "Tetes", "Kapsul"),
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

                Text("Tanggal Pertama Konsumsi", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BiruMuda),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(blueColor)
                    )
                ) {
                    Text(
                        pertamaKonsumsi?.let {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(it)
                        } ?: "Tanggal Pertama Konsumsi"
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Catatan Tambahan") },
                    label = { Text("Masukkan Catatan Tambahan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 2.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

        // Buttons Row (Save + Clear)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val biru = BiruMuda.copy(alpha = 1.0f)

            OutlinedButton(
                onClick = {
                    if (namaObat.isBlank() || jenisObat.isBlank() || satuanDosis.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Data obat tidak boleh kosong!")
                        }
                    } else {
                        val newObat = Obat(
                            id = UUID.randomUUID().toString(), // ✅ Tambahkan ini
                            nama = namaObat,
                            jenis = jenisObat,
                            dosis = satuanDosis,
                            pertamaKonsumsi = pertamaKonsumsi?.let { Timestamp(it) },
                            catatan = notes
                        )

                        // Simpan ke Firestore lewat ViewModel
                        viewModel.addObat(newObat) { success ->
                            scope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar("Data Obat berhasil disimpan")
                                    namaObat = ""
                                    jenisObat = "Tablet"
                                    satuanDosis = "mg"
                                    notes = ""
                                } else {
                                    snackbarHostState.showSnackbar("Gagal menyimpan data")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = biru),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(biru)
                )
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    namaObat = ""
                    jenisObat = "Tablet"
                    satuanDosis = "mg"
                    notes = ""
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = biru),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(biru)
                )
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
