package net.solvetheriddle.fermentlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import net.solvetheriddle.fermentlog.LoginScreen
import net.solvetheriddle.fermentlog.auth.AuthenticationManager
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.ui.screens.ActiveBatchesScreen
import net.solvetheriddle.fermentlog.ui.screens.AddBatchScreen
import net.solvetheriddle.fermentlog.ui.screens.IngredientsScreen
import net.solvetheriddle.fermentlog.ui.screens.SettingsScreen
import net.solvetheriddle.fermentlog.ui.screens.VesselsScreen

private const val LOGIN = "login"
private const val ACTIVE_BATCHES = "active_batches"
private const val ADD_BATCH = "add_batch"
private const val SETTINGS = "settings"
private const val INGREDIENTS = "ingredients"
private const val VESSELS = "vessels"

@Composable
fun AppNavigation(authManager: AuthenticationManager, activeBatches: List<Batch>) {

    val navController = rememberNavController()
    val currentUser: FirebaseUser? by authManager.currentUser.collectAsState()
    val isLoggedIn = currentUser != null
    val startDestination = if (isLoggedIn) ACTIVE_BATCHES else LOGIN
    NavHost(navController = navController, startDestination = startDestination) {
        composable(LOGIN) {
            LoginScreen(
                onLoggedIn = { navController.navigate(ACTIVE_BATCHES) },
                authenticationManager = authManager
            )
        }
        composable(ACTIVE_BATCHES) {
            ActiveBatchesScreen(
                activeBatches = activeBatches,
                onNavigateToSettings = { navController.navigate(SETTINGS) },
                onNavigateToAddBatch = { navController.navigate(ADD_BATCH) }
            )
        }
        composable(SETTINGS) {
            SettingsScreen(
                onNavigateToIngredients = { navController.navigate(INGREDIENTS) },
                onNavigateToVessels = { navController.navigate(VESSELS) },
                onNavigateBack = { navController.popBackStack() },
                onSignOut = {
                    authManager.signOut()
                    navController.navigate(LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(INGREDIENTS) {
            IngredientsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(VESSELS) {
            VesselsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ADD_BATCH) {
            AddBatchScreen(
                onNavigateBack = { navController.popBackStack() },
                onBatchAdded = { newBatch ->
                    Db.addBatch(newBatch)
                    navController.popBackStack()
                }
            )
        }
    }
}
