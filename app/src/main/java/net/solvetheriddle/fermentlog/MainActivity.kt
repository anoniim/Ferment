@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.navigation.AppBottomNavigation
import net.solvetheriddle.fermentlog.ui.screens.AddBatchScreen
import net.solvetheriddle.fermentlog.ui.screens.IngredientsScreen
import net.solvetheriddle.fermentlog.ui.screens.VesselsScreen
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

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
                        activeBatches.addAll(batches.filter { it.status == Status.ACTIVE })
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
                            MainScreen(
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

@Composable
fun MainScreen(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
    ActiveBatchesList(
        activeBatches = activeBatches,
        modifier = modifier
    )
}

@Composable
fun ActiveBatchesList(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(activeBatches) { batch ->
            BatchCard(batch = batch)
        }
    }
}

@Composable
fun BatchCard(batch: Batch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle batch click */ },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = batch.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Phase: ${batch.phase}", fontSize = 14.sp)
            Text(text = "Vessel: ${batch.vessel.name}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Started: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = formatDate(batch.startDate), fontSize = 14.sp)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun formatDate(date: LocalDate): String {
    // Option 1: Using a predefined format
    val predefinedFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    //val predefinedFormatter = DateTimeFormatter.ISO_DATE // Example of a different predefined format
    val formattedDate = date.format(predefinedFormatter.withLocale(getLocale()))

    // Option 2: Using a custom format
//    val customFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", getLocale())
//    val formattedDate2 = date.format(customFormatter)

    // You can return any of the formatted date
    return formattedDate
}

@Composable
@ReadOnlyComposable
fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // Create sample data for preview
    val sampleVessel = Vessel("v1", "Glass Jar", 2.0)
    val sampleIngredient = Ingredient("i1", "Tea")
    val sampleIngredientAmount = IngredientAmount(sampleIngredient, "8 spoons")

    val sampleBatches = listOf(
        Batch(
            id = "b1",
            name = "Kombucha Batch 1",
            status = Status.ACTIVE,
            phase = BrewingPhase.PRIMARY,
            startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            vessel = sampleVessel,
            ingredients = listOf(sampleIngredientAmount)
        ),
        Batch(
            id = "b2",
            name = "Kombucha Batch 2",
            status = Status.ACTIVE,
            phase = BrewingPhase.SECONDARY,
            startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            vessel = sampleVessel,
            ingredients = listOf(sampleIngredientAmount)
        )
    )
    MainScreen(sampleBatches)
}
