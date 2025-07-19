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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
    obatId: String,
    viewModel: ObatViewModel,
    onBackClick: () -> Unit
) {
    val obat by viewModel.obatDetail.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load data saat pertama kali
    LaunchedEffect(obatId) {
        viewModel.getObatById(obatId)
    }

    if (obat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    var namaObat by remember { mutableStateOf(obat!!.nama) }
    var jenisObat by remember { mutableStateOf(obat!!.jenis) }
    var satuanDosis by remember { mutableStateOf(obat!!.dosis) }
    var notes by remember { mutableStateOf(obat!!.catatan ?: "") }

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
                OutlinedTextField(
                    value = namaObat,
                    onValueChange = { namaObat = it },
                    label = { Text("Nama Obat") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Jenis Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("Tablet", "Sirup", "Salep"),
                    selectedOption = jenisObat,
                    onOptionSelected = { jenisObat = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Satuan Dosis", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("mg", "ml", "IU"),
                    selectedOption = satuanDosis,
                    onOptionSelected = { satuanDosis = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        OutlinedButton(
            onClick = {
                val updated = obat!!.copy(
                    nama = namaObat,
                    jenis = jenisObat,
                    dosis = satuanDosis,
                    catatan = notes
                )
                viewModel.updateObat(updated) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "Data berhasil diperbarui" else "Gagal memperbarui data"
                        )
                        if (success) onBackClick()
                    }
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

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
    }
}
