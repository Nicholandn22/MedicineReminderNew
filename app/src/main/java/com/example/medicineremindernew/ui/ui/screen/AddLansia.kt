package com.example.medicineremindernew.ui.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.*

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
    var usia by remember { mutableStateOf("") }
    var golonganDarah by remember { mutableStateOf("A") }
    var penyakit by remember { mutableStateOf("") }
    var nomorWali by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf<Date?>(null) }

    val golonganOptions = listOf("A", "B", "AB", "O")
    val orangeColor = Color(0xFFFF6600)

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                tanggalLahir = Date(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 60.dp)
    ) {
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
                        .padding(bottom = 16.dp)
                )

                Text("Golongan Darah", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = golonganOptions,
                    selectedOption = golonganDarah,
                    onOptionSelected = { golonganDarah = it }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Penyakit", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = penyakit,
                    onValueChange = { penyakit = it },
                    placeholder = { Text("Masukkan penyakit") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text("Nomor Wali", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = nomorWali,
                    onValueChange = { if (it.all { c -> c.isDigit() }) nomorWali = it },
                    placeholder = { Text("Masukkan nomor wali") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text("Tanggal Lahir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = orangeColor),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(orangeColor)
                    )
                ) {
                    Text(tanggalLahir?.toString() ?: "Pilih Tanggal Lahir")
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
                            name = namaLansia,
                            goldar = golonganDarah,
                            gender = "L", // default
                            lahir = tanggalLahir!!,
                            nomorwali = nomorWali.toInt(),
                            penyakit = penyakit
                        )
                        viewModel.insert(lansia)

                        scope.launch {
                            snackbarHostState.showSnackbar("Data Lansia berhasil disimpan")
                        }

                        namaLansia = ""
                        usia = ""
                        penyakit = ""
                        nomorWali = ""
                        golonganDarah = "A"
                        tanggalLahir = null
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Mohon lengkapi semua field")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = orangeColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(orangeColor)
                )
            ) {
                Text("Save")
            }

            OutlinedButton(
                onClick = {
                    namaLansia = ""
                    usia = ""
                    penyakit = ""
                    nomorWali = ""
                    golonganDarah = "A"
                    tanggalLahir = null
                    onCancelClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = orangeColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(orangeColor)
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
