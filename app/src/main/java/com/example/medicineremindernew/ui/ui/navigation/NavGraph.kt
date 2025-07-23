package com.example.medicineremindernew.ui.ui.navigation

//import com.example.medicineremindernew.ui.ui.navigation.BottomNavItem.Lansia
import ObatScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medicineremindernew.ui.ui.screen.AddLansiaScreen
import com.example.medicineremindernew.ui.ui.screen.AddObatScreen
import com.example.medicineremindernew.ui.ui.screen.AddReminderScreen
import com.example.medicineremindernew.ui.ui.screen.DetailLansiaScreen
import com.example.medicineremindernew.ui.ui.screen.DetailObatScreen
import com.example.medicineremindernew.ui.ui.screen.DetailReminderScreen
import com.example.medicineremindernew.ui.ui.screen.HomeScreen
import com.example.medicineremindernew.ui.ui.screen.LansiaScreen
//import com.example.medicineremindernew.ui.ui.screen.LoginScreen
//import com.example.medicineremindernew.ui.ui.screen.RegisterScreen
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    obatViewModel: ObatViewModel,
    lansiaViewModel: LansiaViewModel,
    reminderViewModel: ReminderViewModel,
    modifier: Modifier = Modifier

) {
    NavHost(navController, startDestination = "home",modifier = modifier) {

        // ✅ HOME (Reminder)
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                navController = navController,
                reminderViewModel = reminderViewModel,
                lansiaViewModel = lansiaViewModel,
                obatViewModel = obatViewModel
            )
        }

        // ✅ Tambahan lain
        composable("add_reminder") {
            AddReminderScreen(
                onBackClick = { navController.popBackStack() },
                navController = navController, // ✅ Kirim navController ke sini
                obatViewModel = obatViewModel,
                lansiaViewModel = lansiaViewModel,
                reminderViewModel = reminderViewModel
            )
        }

        composable("addObat") {
            AddObatScreen(
                viewModel = obatViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("addlansia") {
            AddLansiaScreen(
                viewModel = lansiaViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ✅ Lainnya
        composable(BottomNavItem.Lansia.route) { LansiaScreen(navController, lansiaViewModel) }
        composable(BottomNavItem.Obat.route) { ObatScreen(navController, obatViewModel) }

        composable(
            "detail_lansia/{lansiaId}",
            arguments = listOf(navArgument("lansiaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getString("lansiaId") ?: return@composable
            DetailLansiaScreen(
                lansiaId = lansiaId,
                viewModel = lansiaViewModel,
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("detail_obat/{obatId}") { backStackEntry ->
            val obatId = backStackEntry.arguments?.getString("obatId") ?: return@composable
            DetailObatScreen(
                obatId = obatId,
                viewModel = obatViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }


//        composable("detail_reminder/{id}") { backStackEntry ->
//            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
//            DetailReminderScreen(reminderId = id, navController = navController)
//        }

        composable(
            route = "detail_reminder/{reminderId}",
            arguments = listOf(navArgument("reminderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getString("reminderId") ?: return@composable
            DetailReminderScreen(
                reminderId = reminderId,
                navController = navController,
                reminderViewModel = reminderViewModel,
                lansiaViewModel = lansiaViewModel,
                obatViewModel = obatViewModel,
                onBackClick = { navController.popBackStack() },
                onUpdateClick = { navController.popBackStack() }
            )
        }




        // Tambah ini jika kamu punya screen khusus untuk daftar obat/lansia:
        composable("lansia") {
            LansiaScreen(navController, lansiaViewModel)
        }
        composable("obat") {
            ObatScreen(navController, obatViewModel)
        }
    }
}

