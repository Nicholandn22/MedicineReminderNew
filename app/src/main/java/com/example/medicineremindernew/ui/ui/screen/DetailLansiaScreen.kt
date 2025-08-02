package com.example.medicineremindernew.ui.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailLansiaScreen(
    lansiaId: String,
    viewModel: LansiaViewModel,
    navController: NavHostController,
    onBackClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val lansia by viewModel.lansiaDetail.collectAsStateWithLifecycle()

    LaunchedEffect(lansiaId) {
        viewModel.getLansiaById(lansiaId)
    }

    if (lansia == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var namaLansia by remember { mutableStateOf(lansia!!.nama) }
    var golonganDarah by remember { mutableStateOf(lansia!!.goldar) }
    var gender by remember { mutableStateOf(lansia!!.gender) }
    var penyakit by remember { mutableStateOf(lansia!!.penyakit) }
    var nomorWali by remember { mutableStateOf(lansia!!.nomorwali.toString()) }
    var tanggalLahir by remember { mutableStateOf(lansia!!.lahir?.toDate() ?: Date()) }
    var expandedGolongan by remember { mutableStateOf(false) }
    var expandedGender by remember { mutableStateOf(false) }

    val golonganOptions = listOf("A", "B", "AB", "O")
    val genderOptions = listOf("Laki-Laki", "Perempuan")
    val context = LocalContext.current
    val blueColor = BiruMuda.copy(alpha = 1.0f)

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        calendar.time = tanggalLahir
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                tanggalLahir = calendar.time
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
            Text("Update Lansia", color = Color.White, fontSize = 20.sp)
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = namaLansia,
                    onValueChange = { namaLansia = it },
                    label = { Text("Nama Lansia") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // ✅ Golongan Darah
                Text("Golongan Darah", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(expanded = expandedGolongan, onExpandedChange = { expandedGolongan = !expandedGolongan }) {
                    TextField(
                        readOnly = true,
                        value = golonganDarah,
                        onValueChange = {},
                        label = { Text("Pilih golongan darah") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGolongan) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedGolongan, onDismissRequest = { expandedGolongan = false }) {
                        golonganOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                golonganDarah = option
                                expandedGolongan = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Gender
                Text("Jenis Kelamin", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(expanded = expandedGender, onExpandedChange = { expandedGender = !expandedGender }) {
                    TextField(
                        readOnly = true,
                        value = gender,
                        onValueChange = {},
                        label = { Text("Pilih jenis kelamin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                gender = if (option == "Laki-Laki") "L" else "P"
                                expandedGender = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ✅ Penyakit
                OutlinedTextField(
                    value = penyakit,
                    onValueChange = { penyakit = it },
                    label = { Text("Penyakit") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // ✅ Nomor Wali
                OutlinedTextField(
                    value = nomorWali,
                    onValueChange = { if (it.all { c -> c.isDigit() }) nomorWali = it },
                    label = { Text("Nomor Wali") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // ✅ Tanggal Lahir
                Text("Tanggal Lahir", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor)
                ) {
                    Text(SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(tanggalLahir))
                }
            }
        }

        // ✅ Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {
                    if (namaLansia.isNotBlank() && nomorWali.isNotBlank()) {
                        val updatedLansia = lansia!!.copy(
                            nama = namaLansia,
                            goldar = golonganDarah,
                            gender = gender,
                            penyakit = penyakit,
                            nomorwali = nomorWali.toInt(),
                            lahir = Timestamp(tanggalLahir)
                        )
                        viewModel.updateLansia(updatedLansia) { success ->
                            scope.launch {
                                if (success) {
                                    snackbarHostState.showSnackbar("Data berhasil diperbarui")
                                    navController.popBackStack()
                                } else {
                                    snackbarHostState.showSnackbar("Gagal memperbarui data")
                                }
                            }
                        }
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Lengkapi semua data!") }
                    }
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor)
            ) {
                Text("Update")
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = blueColor)
            ) {
                Text("Cancel")
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}

