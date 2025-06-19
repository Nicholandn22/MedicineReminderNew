package com.example.medicineremindernew.ui.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.medicineremindernew.ui.data.local.ObatDatabase
import com.example.medicineremindernew.ui.data.model.Lansia
import com.example.medicineremindernew.ui.data.repository.LansiaRepository
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModelFactory
import kotlinx.coroutines.launch
import java.sql.Date
import java.util.Calendar

@Composable
fun LansiaScreen(
    navController: NavController,
    lansiaViewModel1: LansiaViewModel,
//    context: LansiaViewModel = LocalContext.current
) {

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { ObatDatabase.getDatabase(application) }
    val repository = remember { LansiaRepository(db.lansiaDao()) }
    val viewModelFactory = remember { LansiaViewModelFactory(repository) }

    val lansiaViewModel: LansiaViewModel = viewModel(
        factory = LansiaViewModelFactory(repository)
    )
    val lansiaList = lansiaViewModel.getAllLansia.collectAsState(initial = emptyList()).value
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFC5007))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Lansia Page",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
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
                        color = Color.White
                    )
                } else {
                    lansiaList.forEach { lansia ->
                        val usia = hitungUsiaDariTanggalLahir(java.sql.Date(lansia.lahir.time))
                        LansiaItem(
                            lansia = lansia,
                            usia = usia,
                            onDeleteClick = {
                                lansiaViewModel.delete(lansia)
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

        // Tombol floating Add
        AddLansia(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp),
            onClick = {
                navController.navigate("AddLansia")
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
    onItemClick: () -> Unit // 👈 Tambah parameter

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onItemClick() } // 👈 Navigasi ke detail
    ) {
        Text(
            text = lansia.name,
            fontSize = 20.sp,
            color = Color.Black
        )
        Row {
            Text("Usia : $usia", fontSize = 16.sp, color = Color.DarkGray)
            Text(" | Golongan Darah : ${lansia.goldar}", fontSize = 16.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onDeleteClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Red
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                tint = Color.Red
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hapus", color = Color.Red)
        }
    }
}

fun hitungUsiaDariTanggalLahir(date: Date): Int {
    val dob = Calendar.getInstance()
    dob.time = date

    val today = Calendar.getInstance()

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age
}


@Composable
fun AddLansia(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = OrenMuda,
        contentColor = Color.White,
        text = { Text("Add Lansia") },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah"
            )
        }
    )
}
