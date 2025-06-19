
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.medicineremindernew.ui.data.model.Obat
import com.example.medicineremindernew.ui.data.repository.ObatRepository
import com.example.medicineremindernew.ui.ui.theme.OrenMuda
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModelFactory

@Composable
fun ObatScreen(
    navController: NavController,
    obatViewModel: ObatViewModel,
//    context: ObatViewModel = LocalContext.current
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = remember { ObatDatabase.getDatabase(application) }
    val repository = remember { ObatRepository(db.obatDao()) }
    val viewModelFactory = remember { ObatViewModelFactory(repository) }

    val viewModel: ObatViewModel = viewModel(factory = viewModelFactory)

    val obatList by viewModel.allObat.collectAsState(initial = emptyList())

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
                text = "Obat Page",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
            ) {
                if (obatList.isEmpty()) {
                    Text(
                        text = "Belum ada data obat",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    obatList.forEach { obat ->
                        ObatItem(
                            obat = obat,
                            onDeleteClick = { viewModel.deleteObat(it) },
                            onItemClick = {
                                navController.navigate("DetailObat/${obat.id}")
                            }

                        )
                    }
                }
            }
        }

        // Tombol Tambah Obat
        AddObat(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 120.dp),
            onClick = {
                navController.navigate("AddObat")
            }
        )
    }
}

@Composable
fun ObatItem(
    obat: Obat,
    onDeleteClick: (Obat) -> Unit,
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onItemClick() } // ✅ Tambahkan ini
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = obat.nama,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(text = "Jenis Obat: ${obat.jenis}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Dosis: ${obat.dosis}", fontSize = 16.sp, color = Color.DarkGray)
            Text(text = "Keterangan: ${obat.keterangan}", fontSize = 16.sp, color = Color.DarkGray)
        }

        IconButton(onClick = { onDeleteClick(obat) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun AddObat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = OrenMuda,
        contentColor = Color.White,
        text = {
            Text(text = "Tambah Obat")
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah Obat"
            )
        }
    )
}
