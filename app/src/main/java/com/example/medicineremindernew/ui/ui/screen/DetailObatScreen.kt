package com.example.medicineremindernew.ui.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.res.painterResource
import com.example.medicineremindernew.R


@Composable
fun DetailObatScreen(
    obatId: Int,
    viewModel: ObatViewModel,
    onBackClick: () -> Unit
) {
    val obat by viewModel.getObatById(obatId).collectAsStateWithLifecycle(initialValue = null)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var namaObat by remember { mutableStateOf("") }
    var jenisObat by remember { mutableStateOf("Tablet") }
    var satuanDosis by remember { mutableStateOf("mg") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(obat) {
        obat?.let {
            namaObat = it.nama
            jenisObat = it.jenis
            satuanDosis = it.dosis
            notes = it.keterangan ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(OrenMuda),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                val back_white = 0
                Icon(
                    painter = painterResource(id = R.drawable.back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

            }
            Text(text = "Edit Obat", color = Color.White, fontSize = 20.sp)
        }

        // FORM (sama seperti AddObatScreen)
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nama Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = namaObat,
                    onValueChange = { namaObat = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
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

                Text("Notes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
            }
        }

        Button(
            onClick = {
                obat?.let {
                    val updated = it.copy(
                        nama = namaObat,
                        jenis = jenisObat,
                        dosis = satuanDosis,
                        keterangan = notes
                    )
                    viewModel.updateObat(updated)
                    scope.launch {
                        snackbarHostState.showSnackbar("Data berhasil diperbarui")
                    }
                    onBackClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Update")
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

