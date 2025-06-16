package com.example.medicineremindernew.ui.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun DetailLansiaScreen(
    lansiaId: Int,
    viewModel: LansiaViewModel,
    navController: NavHostController,
    onBackClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Ambil data lansia dari ViewModel
    val lansia by viewModel.getLansiaById(lansiaId).collectAsStateWithLifecycle(initialValue = null)

    if (lansia == null) {
        // Loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var namaLansia by remember { mutableStateOf(lansia!!.name) }
    var golonganDarah by remember { mutableStateOf(lansia!!.goldar) }
    var penyakit by remember { mutableStateOf(lansia!!.penyakit ?: "") }
    var nomorWali by remember { mutableStateOf(lansia!!.nomorwali.toString()) }
    var tanggalLahir by remember { mutableStateOf<Date?>(lansia!!.lahir) }

    val golonganOptions = listOf("A", "B", "AB", "O")
    val context = LocalContext.current

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
            .padding(16.dp)
    ) {
        TextField(
            value = namaLansia,
            onValueChange = { namaLansia = it },
            label = { Text("Nama") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        DropdownMenuField(
            options = golonganOptions,
            selectedOption = golonganDarah,
            onOptionSelected = { golonganDarah = it }
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = penyakit,
            onValueChange = { penyakit = it },
            label = { Text("Penyakit") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        TextField(
            value = nomorWali,
            onValueChange = { if (it.all { c -> c.isDigit() }) nomorWali = it },
            label = { Text("Nomor Wali") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Button(onClick = { datePickerDialog.show() }) {
            Text(text = tanggalLahir?.toString() ?: "Pilih Tanggal Lahir")
        }

        Spacer(Modifier.height(16.dp))
        Button(
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
                    navController.popBackStack() // INI YANG MENJALANKAN BACK
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Mohon lengkapi semua data")
                    }
                }
            }
        ) {
            Text("Update Data")
        }

        SnackbarHost(snackbarHostState)
    }
}
