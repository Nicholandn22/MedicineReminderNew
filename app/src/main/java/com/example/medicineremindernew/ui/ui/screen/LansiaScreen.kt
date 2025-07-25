package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.ui.theme.BiruTua
import com.example.medicineremindernew.ui.ui.theme.Krem
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModelFactory
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar

@Composable
fun LansiaScreen(
    navController: NavController,
    lansiaViewModel: LansiaViewModel
) {
    val lansiaList by lansiaViewModel.lansiaList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val warnaKrem = Krem.copy(alpha = 1.0f)
    val warnaBiru = BiruTua.copy(alpha = 1.0f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(warnaKrem)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Daftar Lansia",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = warnaBiru
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                if (lansiaList.isEmpty()) {
                    Text(
                        text = "Belum ada data lansia",
                        modifier = Modifier.padding(16.dp),
                        color = warnaBiru
                    )
                } else {
                    lansiaList.forEach { lansia ->
                        val usia = hitungUsiaDariTanggalLahir(lansia.lahir)
                        LansiaItem(
                            lansia = lansia,
                            usia = usia,
                            onDeleteClick = {
                                lansiaViewModel.deleteLansia(lansia.id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Lansia berhasil dihapus")
                                }
                            },
                            onItemClick = {
                                navController.navigate("detail_lansia/${lansia.id}")
                            }
                        )
                    }
                }
            }
        }

        // ✅ Tombol floating Add
        AddLansia(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp),
            onClick = {
                navController.navigate("addlansia")
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun LansiaItem(
    lansia: Lansia,
    usia: Int,
    onDeleteClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onItemClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = lansia.nama, fontSize = 20.sp, color = Color.Black)
            Row {
                Text("Usia : $usia", fontSize = 16.sp, color = Color.DarkGray)
                Text(" | Golongan Darah : ${lansia.goldar}", fontSize = 16.sp, color = Color.DarkGray)
            }
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun hitungUsiaDariTanggalLahir(lahir: Timestamp?): Int {
    if (lahir == null) return 0
    val dob = Calendar.getInstance()
    dob.time = lahir.toDate()
    val today = Calendar.getInstance()
    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}

@Composable
fun AddLansia(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = BiruTua.copy(alpha = 1.0f),
        contentColor = Color.White
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(30.dp))
    }
}
