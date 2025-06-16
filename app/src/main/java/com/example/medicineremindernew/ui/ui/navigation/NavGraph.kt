package com.example.medicineremindernew.ui.ui.navigation

//import com.example.medicineremindernew.ui.ui.navigation.BottomNavItem.Lansia
import ObatScreen
import androidx.compose.runtime.Composable
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
import com.example.medicineremindernew.ui.ui.screen.HomeScreen
import com.example.medicineremindernew.ui.ui.screen.LansiaScreen
import com.example.medicineremindernew.ui.ui.screen.LoginScreen
import com.example.medicineremindernew.ui.ui.screen.RegisterScreen
import com.example.medicineremindernew.ui.ui.viewmodel.AuthViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.LansiaViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ObatViewModel
import com.example.medicineremindernew.ui.ui.viewmodel.ReminderViewModel


@Composable
fun NavGraph(
    navController: NavHostController,
    obatViewModel: ObatViewModel,
    lansiaViewModel: LansiaViewModel,
    reminderViewModel: ReminderViewModel,
    authViewModel: AuthViewModel // ✅ Tambahkan ViewModel Auth
) {
    NavHost(navController, startDestination = "login") { // ✅ Awali dari login
        // ✅ LOGIN SCREEN
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("login") { inclusive = true } // Hapus login dari backstack
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        // ✅ REGISTER SCREEN
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // ✅ HOME (Reminder)
        composable(BottomNavItem.Home.route) {
            HomeScreen(
                navController = navController,
                reminderViewModel = reminderViewModel
            )
        }

        // ✅ Tambahan lain
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

        // ✅ Lainnya
        composable(BottomNavItem.Lansia.route) { LansiaScreen(navController) }
        composable(BottomNavItem.Obat.route) { ObatScreen(navController) }

        composable(
            "detail_lansia/{lansiaId}",
            arguments = listOf(navArgument("lansiaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lansiaId = backStackEntry.arguments?.getInt("lansiaId") ?: return@composable
            DetailLansiaScreen(lansiaId = lansiaId, viewModel = lansiaViewModel,navController = navController )
        }

        composable("DetailObat/{obatId}") { backStackEntry ->
            val obatId = backStackEntry.arguments?.getString("obatId")?.toIntOrNull()
            if (obatId != null) {
                DetailObatScreen(
                    obatId = obatId,
                    viewModel = obatViewModel, // pastikan ini dari parent
                    onBackClick = { navController.popBackStack() }
                )
            }
        }







    }
}

