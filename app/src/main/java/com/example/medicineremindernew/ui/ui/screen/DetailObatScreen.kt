package com.example.medicineremindernew.ui.ui.screen

import android.content.Context
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.material3.TextButton
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
import com.example.medicineremindernew.R.drawable.back_white
import com.example.medicineremindernew.ui.ui.theme.BiruAgakTua
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.viewmodel.HybridObatViewModel
import kotlinx.coroutines.launch

@Composable
fun DetailObatScreen(
    obatId: String,
    viewModel: HybridObatViewModel,
    onBackClick: () -> Unit
) {
    val obat by viewModel.obatDetail.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val blueColor = BiruMuda.copy(alpha = 1.0f)


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
    var waktuMinum by remember { mutableStateOf(obat!!.waktuMinum) }
    var stok by remember { mutableStateOf(obat!!.stok.toString()) }
    var notes by remember { mutableStateOf(obat!!.catatan ?: "") }
    var takaranDosis by remember { mutableStateOf(obat!!.takaranDosis) }

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("satuan_dosis", Context.MODE_PRIVATE)

    val defaultSatuan = listOf("mg", "ml", "IU", "Tetes")
    val savedSatuan = sharedPrefs.getStringSet("custom_satuan", emptySet())?.toList() ?: emptyList()
    var listSatuanDosis by remember { mutableStateOf((defaultSatuan + savedSatuan).distinct().toMutableList()) }
//    var listSatuanDosis by remember { mutableStateOf(mutableListOf("mg", "ml", "IU", "Tetes")) }
    var inputSatuanBaru by remember { mutableStateOf(false) }
    var satuanBaru by remember { mutableStateOf("") }

    // Fungsi untuk menyimpan satuan baru ke SharedPreferences
    fun saveSatuanToPrefs(newSatuan: String) {
        val currentSaved = sharedPrefs.getStringSet("custom_satuan", emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSaved.add(newSatuan)
        sharedPrefs.edit().putStringSet("custom_satuan", currentSaved).apply()
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
            Text("Update Obat", color = Color.White, fontSize = 20.sp)
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
                    options = listOf("Tablet", "Sirup", "Salep", "Tetes", "Kapsul"),
                    selectedOption = jenisObat,
                    onOptionSelected = { jenisObat = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Dosis Obat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // ini buat masukkin takaran
                    OutlinedTextField(
                        value = takaranDosis,
                        onValueChange = {takaranDosis = it},
                        placeholder = {Text ("Takaran")},
                        label = {Text ("Takaran")},
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    DropdownMenuField(
                        options = listSatuanDosis,
                        selectedOption = satuanDosis,
                        onOptionSelected = { selected ->
                            satuanDosis = selected
                            inputSatuanBaru = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Card(
                        modifier = Modifier.size(56.dp),
                        colors = CardDefaults.cardColors(containerColor = BiruMuda),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                inputSatuanBaru = !inputSatuanBaru
                                satuanBaru = ""
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Satuan Baru",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                if (inputSatuanBaru) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = satuanBaru,
                            onValueChange = { satuanBaru = it },
                            placeholder = { Text("Satuan baru") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (satuanBaru.isNotBlank() && !listSatuanDosis.contains(satuanBaru)) {
                                    listSatuanDosis = (listSatuanDosis + satuanBaru).toMutableList()
                                    saveSatuanToPrefs(satuanBaru) // Simpan ke SharedPreferences
                                    satuanDosis = satuanBaru
                                    satuanBaru = ""
                                    inputSatuanBaru = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Satuan '$satuanDosis' berhasil ditambahkan!")
                                    }
                                } else if (listSatuanDosis.contains(satuanBaru)) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Satuan sudah ada!")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Satuan tidak boleh kosong!")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BiruMuda),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(
                                "Add",
                                color = Color.White,
                                fontSize = 16.sp, // Font lebih besar
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Tombol batal dalam satu baris
                    TextButton(
                        onClick = {
                            inputSatuanBaru = false
                            satuanBaru = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal", color = BiruMuda, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text("Waktu Minum", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                DropdownMenuField(
                    options = listOf("Sebelum Makan", "Sesudah Makan"),
                    selectedOption = waktuMinum,
                    onOptionSelected = { waktuMinum = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = stok,
                    onValueChange = { if (it.all { c -> c.isDigit() }) stok = it },
                    label = { Text("Stok Obat") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

        // ✅ Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OutlinedButton(
                onClick = {
                    val updated = obat!!.copy(
                        nama = namaObat,
                        jenis = jenisObat,
                        dosis = satuanDosis,
                        waktuMinum = waktuMinum,
                        catatan = notes,
                        stok = stok.toIntOrNull() ?: 0, // ✅ update stok
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
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
    }
}
