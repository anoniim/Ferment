package net.solvetheriddle.fermentlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.ui.screens.ActiveBatchesScreen
import net.solvetheriddle.fermentlog.ui.screens.AddBatchScreen
import net.solvetheriddle.fermentlog.ui.screens.IngredientsScreen
import net.solvetheriddle.fermentlog.ui.screens.SettingsScreen
import net.solvetheriddle.fermentlog.ui.screens.VesselsScreen

@Composable
fun AppNavigation(activeBatches: List<Batch>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "active_batches") {
        composable("active_batches") {
            ActiveBatchesScreen(
                activeBatches = activeBatches,
                onNavigateToSettings = { println("settings clicked"); navController.navigate("settings") },
                onNavigateToAddBatch = { navController.navigate("add_batch") }
            )
        }
        composable("settings") {
            println("open settings")
            SettingsScreen(
                onNavigateToIngredients = { navController.navigate("ingredients") },
                onNavigateToVessels = { navController.navigate("vessels") },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("ingredients") {
            IngredientsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("vessels") {
            VesselsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("add_batch") {
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
