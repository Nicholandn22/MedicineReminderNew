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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.medicineremindernew.R
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import kotlinx.coroutines.launch

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
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_white),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(text = "Edit Obat", color = Color.White, fontSize = 20.sp)
        }

        // FORM
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

                Text("Notes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
            }
        }

        // TOMBOL UPDATE
        OutlinedButton(
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
                .padding(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrenMuda),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(OrenMuda)
            )
        ) {
            Text("Update")
        }

        // SNACKBAR
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
