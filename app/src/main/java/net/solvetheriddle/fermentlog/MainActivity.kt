@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.navigation.AppBottomNavigation
import net.solvetheriddle.fermentlog.ui.screens.ActiveBatchesScreen
import net.solvetheriddle.fermentlog.ui.screens.AddBatchScreen
import net.solvetheriddle.fermentlog.ui.screens.IngredientsScreen
import net.solvetheriddle.fermentlog.ui.screens.VesselsScreen
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FermentTheme {
                val activeBatches = remember { mutableStateListOf<Batch>() }
                val showAddBatchScreen = remember { androidx.compose.runtime.mutableStateOf(false) }
                val showVesselsScreen = remember { androidx.compose.runtime.mutableStateOf(false) }
                val showIngredientsScreen = remember { androidx.compose.runtime.mutableStateOf(false) }

                // Collect batches from Firebase
                LaunchedEffect(Unit) {
                    Db.getBatchesFlow().collect { batches ->
                        activeBatches.clear()
                        activeBatches.addAll(batches.filter { it.status == Status.ACTIVE }) // TODO Order by phase, then duration
                    }
                }

                when {
                    showAddBatchScreen.value -> {
                        AddBatchScreen(
                            onNavigateBack = { showAddBatchScreen.value = false },
                            onBatchAdded = { newBatch ->
                                Db.addBatch(newBatch)
                                // The batch will be added to the list via the Flow collection
                            }
                        )
                    }
                    showVesselsScreen.value -> {
                        VesselsScreen(
                            onNavigateToActive = { 
                                showVesselsScreen.value = false 
                            },
                            onNavigateToIngredients = { 
                                showVesselsScreen.value = false
                                showIngredientsScreen.value = true
                            }
                        )
                    }
                    showIngredientsScreen.value -> {
                        IngredientsScreen(
                            onNavigateToActive = {
                                showIngredientsScreen.value = false
                            },
                            onNavigateToVessels = {
                                showIngredientsScreen.value = false
                                showVesselsScreen.value = true
                            }
                        )
                    }
                    else -> {
                        Scaffold(
                            topBar = { TopAppBar(title = { Text("Active Batches") }) },
                            floatingActionButton = {
                                FloatingActionButton(onClick = { showAddBatchScreen.value = true }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Batch")
                                }
                            },
                            bottomBar = {
                                AppBottomNavigation(
                                    currentRoute = "active",
                                    onNavigateToActive = { /* Already on active screen */ },
                                    onNavigateToIngredients = { showIngredientsScreen.value = true },
                                    onNavigateToVessels = { showVesselsScreen.value = true }
                                )
                            }
                        ) { innerPadding ->
                            ActiveBatchesScreen(
                                activeBatches = activeBatches,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

