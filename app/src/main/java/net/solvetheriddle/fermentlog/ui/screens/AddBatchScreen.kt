@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.time.ZoneId
import java.util.Date

@Composable
fun AddBatchScreen(
    onNavigateBack: () -> Unit,
    onBatchAdded: (Batch) -> Unit
) {
    var batchName by remember { mutableStateOf("") }
    var selectedPhase by remember { mutableStateOf(BrewingPhase.PRIMARY) }

    // Fetch vessels from database
    val vessels = remember { mutableStateListOf<Vessel>() }
    var selectedVessel by remember { mutableStateOf<Vessel?>(null) }
    var showVesselDropdown by remember { mutableStateOf(false) }

    // Fetch ingredients from database
    val ingredients = remember { mutableStateListOf<Ingredient>() }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var showIngredientDropdown by remember { mutableStateOf(false) }

    var ingredientAmount by remember { mutableStateOf("") }
    val ingredientAmounts = remember { mutableStateListOf<IngredientAmount>() }

    // Collect vessels from Firebase
    LaunchedEffect(Unit) {
        Db.getVesselsFlow().collect { fetchedVessels ->
            vessels.clear()
            vessels.addAll(fetchedVessels)
            if (vessels.isNotEmpty() && selectedVessel == null) {
                selectedVessel = vessels.first()
            }
        }
    }

    // Collect ingredients from Firebase
    LaunchedEffect(Unit) {
        Db.getIngredientsFlow().collect { fetchedIngredients ->
            ingredients.clear()
            ingredients.addAll(fetchedIngredients)
            if (ingredients.isNotEmpty() && selectedIngredient == null) {
                selectedIngredient = ingredients.first()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Batch") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Batch Name
            OutlinedTextField(
                value = batchName,
                onValueChange = { batchName = it },
                label = { Text("Batch Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Brewing Phase
            Text("Brewing Phase", style = MaterialTheme.typography.bodyLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BrewingPhase.entries.forEach { phase ->
                    FilterChip(
                        selected = selectedPhase == phase,
                        onClick = { selectedPhase = phase },
                        label = { Text(phase.name) }
                    )
                }
            }

            // Vessel Selection
            Text("Vessel", style = MaterialTheme.typography.bodyLarge)

            if (vessels.isEmpty()) {
                Text("No vessels available. Please add vessels first.")
            } else {
                // Dropdown for vessel selection
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedVessel?.let {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = it.name)
                                    Text(text = "Capacity: ${it.capacity} L")
                                }
                            }

                            IconButton(onClick = { showVesselDropdown = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Vessel")
                            }

                            DropdownMenu(
                                expanded = showVesselDropdown,
                                onDismissRequest = { showVesselDropdown = false }
                            ) {
                                vessels.forEach { vessel ->
                                    DropdownMenuItem(
                                        text = { Text("${vessel.name} (${vessel.capacity} L)") },
                                        onClick = {
                                            selectedVessel = vessel
                                            showVesselDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Ingredients
            Text("Ingredients", style = MaterialTheme.typography.bodyLarge)

            // Add ingredient form
            if (ingredients.isEmpty()) {
                Text("No ingredients available. Please add ingredients first.")
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dropdown for ingredient selection
                    Card(
                        modifier = Modifier.weight(1f),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            selectedIngredient?.let {
                                Text(
                                    text = it.name,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            IconButton(onClick = { showIngredientDropdown = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Ingredient")
                            }

                            DropdownMenu(
                                expanded = showIngredientDropdown,
                                onDismissRequest = { showIngredientDropdown = false }
                            ) {
                                ingredients.forEach { ingredient ->
                                    DropdownMenuItem(
                                        text = { Text(ingredient.name) },
                                        onClick = {
                                            selectedIngredient = ingredient
                                            showIngredientDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ingredientAmount,
                        onValueChange = { ingredientAmount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            if (ingredientAmount.isNotBlank() && selectedIngredient != null) {
                                ingredientAmounts.add(
                                    IngredientAmount(
                                        ingredient = selectedIngredient!!,
                                        amount = ingredientAmount
                                    )
                                )
                                ingredientAmount = ""
                            }
                        },
                        enabled = ingredientAmount.isNotBlank() && selectedIngredient != null
                    ) {
                        Text("Add")
                    }
                }
            }

            // Ingredient list
            if (ingredientAmounts.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Added Ingredients:", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        ingredientAmounts.forEach { ingredientAmount ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = ingredientAmount.ingredient.name)
                                Text(text = ingredientAmount.amount)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            } else {
                // Show placeholder if no ingredients added
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No ingredients added yet")
                    }
                }
            }

            // Add Batch Button
            Button(
                onClick = {
                    selectedVessel?.let { vessel ->
                        val newBatch = Batch(
                            name = batchName,
                            startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                            vessel = vessel,
                            ingredients = ingredientAmounts
                        )
                        onBatchAdded(newBatch)
                        onNavigateBack()
                    }
                },
                enabled = selectedVessel != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Add Batch")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBatchScreenPreview() {
    // This is just a preview, in the real app data will be fetched from Firebase
    AddBatchScreen(
        onNavigateBack = {},
        onBatchAdded = {}
    )
}
