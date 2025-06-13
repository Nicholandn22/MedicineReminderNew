package com.example.medicineremindernew.ui.ui.navigation

import ObatScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicineremindernew.ui.ui.screen.AddLansiaScreen
import com.example.medicineremindernew.ui.ui.screen.AddObatScreen
import com.example.medicineremindernew.ui.ui.screen.AddReminderScreen
import com.example.medicineremindernew.ui.ui.screen.HomeScreen
import com.example.medicineremindernew.ui.ui.screen.LansiaScreen
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel

@Composable
fun NavGraph(navController: NavHostController,obatViewModel: ObatViewModel, lansiaViewModel: LansiaViewModel) {
    NavHost(navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen(navController) }
        composable("add_reminder") {
            AddReminderScreen(
                onBackClick = { navController.popBackStack() }
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

//        composable(BottomNavItem.Home.route) {
//            HomeScreen(navController = navController)
//        }

        composable(BottomNavItem.Lansia.route) { LansiaScreen(navController) }
        composable(BottomNavItem.Obat.route) { ObatScreen(navController) }
    }
}
