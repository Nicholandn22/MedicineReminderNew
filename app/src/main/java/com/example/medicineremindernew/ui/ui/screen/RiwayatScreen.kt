// RiwayatScreen.kt
package com.example.medicineremindernew.ui.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.model.Riwayat
import com.example.medicineremindernew.ui.ui.theme.BiruMuda
import com.example.medicineremindernew.ui.ui.theme.BiruTua
import com.example.medicineremindernew.ui.ui.theme.Krem
import com.example.medicineremindernew.ui.ui.viewmodel.HybridLansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.HybridRiwayatViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*


/**
 * Ui model internal yang lebih fleksibel — dibangun dari dokumen Firestore riwayat
 */
private data class UiRiwayat(
    val id: String,
    val lansiaIds: List<String> = emptyList(),
    val obatIds: List<String> = emptyList(),
    val jenis: String? = null,
    val keterangan: String? = null,
    val tanggal: String? = null,   // optional: "2025-09-20"
    val waktu: String? = null,     // optional: "08:00"
    val waktuDiminumMillis: Long? = null // optional: epoch millis
)

/**
 * RiwayatScreen (1 file). Gunakan dengan:
 * RiwayatScreen(navController, riwayatViewModel, lansiaViewModel, obatViewModel)
 */
@Composable
fun RiwayatScreen(
    navController: NavController,
    riwayatViewModel: HybridRiwayatViewModel,   // (bisa dipakai untuk delete/refresh jika mau)
    lansiaViewModel: HybridLansiaViewModel,
    obatViewModel: HybridObatViewModel
) {
    val lansiaList by lansiaViewModel.lansiaList.collectAsState()
    val obatList by obatViewModel.obatList.collectAsState()

    var uiRiwayatList by remember { mutableStateOf<List<UiRiwayat>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var riwayatToDelete by remember { mutableStateOf<String?>(null) }

    // Build quick maps id->nama for lookup
    val lansiaMap by remember(lansiaList) {
        mutableStateOf(lansiaList.associate { it.id to (it.nama.ifBlank { "Nama tidak tersedia" }) })
    }
    val obatMap by remember(obatList) {
        mutableStateOf(obatList.associate { it.id to (it.nama.ifBlank { "Nama tidak tersedia" }) })
    }

    // Fetch riwayat langsung dari Firestore (ambil semua)
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val db = FirebaseFirestore.getInstance()
            val snap = db.collection("riwayat").get().await()
            val list = snap.documents.mapNotNull { doc ->
                try {
                    // flexible parsing: accept "lansiaIds" list OR "lansiaId" single
                    val lansiaIds = when (val x = doc.get("lansiaIds")) {
                        is List<*> -> x.mapNotNull { it?.toString() }
                        else -> {
                            val single = doc.getString("lansiaId")
                            if (single != null) listOf(single) else emptyList()
                        }
                    }

                    // obat ids
                    val obatIds = when (val x = doc.get("obatIds")) {
                        is List<*> -> x.mapNotNull { it?.toString() }
                        else -> {
                            val single = doc.getString("obatId")
                            if (single != null) listOf(single) else emptyList()
                        }
                    }

                    // waktuDiminum mungkin disimpan sebagai number (Long/Double) or string
                    val waktuDiminumMillis: Long? = when (val x = doc.get("waktuDiminum")) {
                        is Long -> x
                        is Double -> x.toLong()
                        is Number -> x.toLong()
                        is String -> x.toLongOrNull()
                        else -> null
                    }

                    // tanggal / waktu (string)
                    val tanggal = doc.getString("tanggal")
                    val waktu = doc.getString("waktu")

                    val jenis = doc.getString("jenis") ?: doc.getString("status")
                    val keterangan = doc.getString("keterangan") ?: doc.getString("catatan")

                    UiRiwayat(
                        id = doc.id,
                        lansiaIds = lansiaIds,
                        obatIds = obatIds,
                        jenis = jenis,
                        keterangan = keterangan,
                        tanggal = tanggal,
                        waktu = waktu,
                        waktuDiminumMillis = waktuDiminumMillis
                    )
                } catch (e: Exception) {
                    Log.e("RiwayatScreen", "Gagal parsing doc ${doc.id}: ${e.message}")
                    null
                }
            }
            // sort by time: prefer waktuDiminumMillis, else try tanggal+waktu, else keep order
            uiRiwayatList = list.sortedWith(compareByDescending<UiRiwayat> {
                it.waktuDiminumMillis ?: run {
                    // parse tanggal + waktu -> millis
                    val t = try {
                        if (!it.tanggal.isNullOrBlank() && !it.waktu.isNullOrBlank()) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            sdf.parse("${it.tanggal} ${it.waktu}")?.time
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                    t
                }
            })
        } catch (e: Exception) {
            Log.e("RiwayatScreen", "Gagal ambil riwayat: ${e.message}", e)
            uiRiwayatList = emptyList()
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Krem)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Riwayat Minum Obat",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = BiruTua
            )

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (uiRiwayatList.isEmpty()) {
                        Text("Belum ada riwayat.", modifier = Modifier.padding(8.dp), color = Color.Gray)
                    } else {
                        uiRiwayatList.forEach { r ->
                            // map ids -> nama (join)
                            val namaLansia = if (r.lansiaIds.isNotEmpty()) {
                                r.lansiaIds.map { id -> lansiaMap[id] ?: id }.joinToString(", ")
                            } else {
                                "(tidak ada lansia)"
                            }
                            val namaObat = if (r.obatIds.isNotEmpty()) {
                                r.obatIds.map { id -> obatMap[id] ?: id }.joinToString(", ")
                            } else {
                                "(tidak ada obat)"
                            }

                            val waktuText = remember(r) {
                                r.waktuDiminumMillis?.let {
                                    try {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                        sdf.format(Date(it))
                                    } catch (e: Exception) {
                                        "-"
                                    }
                                } ?: run {
                                    if (!r.tanggal.isNullOrBlank() && !r.waktu.isNullOrBlank()) {
                                        "${r.tanggal} ${r.waktu}"
                                    } else {
                                        "-"
                                    }
                                }
                            }

                            RiwayatRow(
                                riwayatId = r.id,
                                lansiaNama = namaLansia,
                                obatNama = namaObat,
                                waktuText = waktuText,
                                jenis = r.jenis ?: r.keterangan ?: "-",
                                onDelete = {
                                    riwayatToDelete = r.id
                                    showDeleteDialog = true
                                },
                                onClick = {
                                    // optional: navigasi ke detail riwayat
                                    navController.navigate("detail_riwayat/${r.id}")
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        // tombol tambah (opsional)
        FloatingActionButton(
            onClick = { navController.navigate("add_riwayat") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp),
            containerColor = BiruTua,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah")
        }

        // dialog hapus
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Konfirmasi Hapus", fontWeight = FontWeight.Bold, color = BiruMuda) },
                text = { Text("Apakah Anda yakin ingin menghapus riwayat ini?") },
                confirmButton = {
                    TextButton(onClick = {
                        val id = riwayatToDelete
                        if (id != null) {
                            // hapus dokumen Firestore
                            try {
                                val db = FirebaseFirestore.getInstance()
                                db.collection("riwayat").document(id).delete()
                                    .addOnSuccessListener {
                                        Log.d("RiwayatScreen", "Riwayat $id dihapus")
                                        // refresh list (simple: reload)
                                        // NOTE: bisa optimalkan dengan remove dari uiRiwayatList langsung
                                        // tapi untuk kesederhanaan kita panggil ulang fetch dengan trigger
                                        // => set uiRiwayatList = uiRiwayatList.filter { it.id != id }
                                        uiRiwayatList = uiRiwayatList.filter { it.id != id }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RiwayatScreen", "Gagal hapus riwayat $id: ${e.message}", e)
                                    }
                            } catch (e: Exception) {
                                Log.e("RiwayatScreen", "Exception saat hapus: ${e.message}", e)
                            }
                        }
                        showDeleteDialog = false
                    }) {
                        Text("Ya", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Tidak", color = BiruMuda)
                    }
                }
            )
        }
    }
}

@Composable
private fun RiwayatRow(
    riwayatId: String,
    lansiaNama: String,
    obatNama: String,
    waktuText: String,
    jenis: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Lansia: $lansiaNama", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Obat: $obatNama", fontSize = 16.sp, color = Color.DarkGray)
            Text("Waktu: $waktuText", fontSize = 14.sp, color = Color.Gray)
            Text("Jenis: $jenis", fontSize = 14.sp, color = Color(0xFF555555))
        }

        Column(horizontalAlignment = Alignment.End) {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
            }
        }
    }
}
