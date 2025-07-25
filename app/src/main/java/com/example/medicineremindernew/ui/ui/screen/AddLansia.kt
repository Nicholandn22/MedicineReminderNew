package com.example.medicineremindernew.ui.ui.screen

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.Timestamp


@Composable
fun AddLansiaScreen(
    viewModel: LansiaViewModel,
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var namaLansia by remember { mutableStateOf("") }
    var penyakit by remember { mutableStateOf("") }
    var nomorWali by remember { mutableStateOf("") }
    var golonganDarah by remember { mutableStateOf("A") }
    var tanggalLahir by remember { mutableStateOf<Date?>(null) }

    val golonganOptions = listOf("A", "B", "AB", "O")
    val blueColor = BiruMuda.copy(alpha = 1.0f)

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            tanggalLahir = calendar.time
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
            Text("Tambah Lansia", color = Color.White, fontSize = 20.sp)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = namaLansia,
                    onValueChange = { namaLansia = it },
                    placeholder = { Text("Nama Lansia") },
                    label = { Text("Masukkan Nama Lansia") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                Text("Golongan Darah", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = golonganOptions,
                    selectedOption = golonganDarah,
                    onOptionSelected = { golonganDarah = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = penyakit,
                    onValueChange = { penyakit = it },
                    placeholder = { Text("Penyakit") },
                    label = { Text("Masukkan Penyakit") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = nomorWali,
                    onValueChange = { if (it.all { c -> c.isDigit() }) nomorWali = it },
                    placeholder = { Text("Nomor Wali") },
                    label = { Text("Masukkan Nomor Wali") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                Text("Tanggal Lahir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(blueColor)
                    )
                ) {
                    Text(
                        tanggalLahir?.let {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(it)
                        } ?: "Pilih Tanggal Lahir"
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {
                    if (
                        namaLansia.isNotBlank() &&
                        nomorWali.isNotBlank() &&
                        tanggalLahir != null
                    ) {
                        val lansia = Lansia(
                            nama = namaLansia,
                            goldar = golonganDarah,
                            gender = "L", // Default
                            lahir = Timestamp(tanggalLahir!!), // ✅ Konversi ke Firestore Timestamp
                            nomorwali = nomorWali.toInt(),
                            penyakit = penyakit
                        )


                        viewModel.addLansia(lansia) { success ->
                            scope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar("Data Lansia berhasil disimpan")
                                    // Reset form
                                    namaLansia = ""
                                    penyakit = ""
                                    nomorWali = ""
                                    golonganDarah = "A"
                                    tanggalLahir = null
                                } else {
                                    snackbarHostState.showSnackbar("Gagal menyimpan data")
                                }
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Mohon lengkapi semua field")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(blueColor)
                )
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    namaLansia = ""
                    penyakit = ""
                    nomorWali = ""
                    golonganDarah = "A"
                    tanggalLahir = null
                    onCancelClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(blueColor)
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
