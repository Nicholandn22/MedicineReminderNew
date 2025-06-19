package com.example.medicineremindernew.ui.ui.screen

import android.app.DatePickerDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLansiaScreen(
    lansiaId: Int,
    viewModel: LansiaViewModel,
    navController: NavHostController,
    onBackClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val lansia by viewModel.getLansiaById(lansiaId).collectAsStateWithLifecycle(initialValue = null)

    if (lansia == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var namaLansia by remember { mutableStateOf(lansia!!.name) }
    var golonganDarah by remember { mutableStateOf(lansia!!.goldar) }
    var expanded by remember { mutableStateOf(false) }
    var penyakit by remember { mutableStateOf(lansia!!.penyakit ?: "") }
    var nomorWali by remember { mutableStateOf(lansia!!.nomorwali.toString()) }
    var tanggalLahir by remember { mutableStateOf<Date?>(lansia!!.lahir) }

    val golonganOptions = listOf("A", "B", "AB", "O")
    val context = LocalContext.current
    val orangeColor = Color(0xFFFF6600)

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        calendar.time = tanggalLahir ?: Date(System.currentTimeMillis())
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
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        readOnly = true,
                        value = golonganDarah,
                        onValueChange = {},
                        label = { Text("Pilih golongan darah") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        golonganOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    golonganDarah = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

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
                    if (tanggalLahir != null && namaLansia.isNotBlank()) {
                        val updatedLansia = lansia!!.copy(
                            name = namaLansia,
                            goldar = golonganDarah,
                            penyakit = penyakit,
                            nomorwali = nomorWali.toInt(),
                            lahir = tanggalLahir!!
                        )
                        viewModel.update(updatedLansia)
                        scope.launch {
                            snackbarHostState.showSnackbar("Data berhasil diperbarui")
                        }
                        navController.popBackStack()
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Mohon lengkapi semua data")
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
                Text("Update")
            }

            OutlinedButton(
                onClick = {
                    onBackClick()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = orangeColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(orangeColor)
                )
            ) {
                Text("Cancel")
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
