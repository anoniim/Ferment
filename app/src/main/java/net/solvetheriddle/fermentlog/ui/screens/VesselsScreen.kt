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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.navigation.AppBottomNavigation

@Composable
fun VesselsScreen(
    onNavigateToActive: () -> Unit,
    onNavigateToIngredients: () -> Unit
) {
    val vessels = remember { mutableStateListOf<Vessel>() }
    val showAddVesselDialog = remember { mutableStateOf(false) }
    val vesselToEdit = remember { mutableStateOf<Vessel?>(null) }
    val vesselToDelete = remember { mutableStateOf<Vessel?>(null) }

    // Collect vessels from Firebase
    LaunchedEffect(Unit) {
        Db.getVesselsFlow().collect { fetchedVessels ->
            vessels.clear()
            vessels.addAll(fetchedVessels)
        }
    }

    if (showAddVesselDialog.value || vesselToEdit.value != null) {
        AddEditVesselDialog(
            vessel = vesselToEdit.value,
            onDismiss = {
                showAddVesselDialog.value = false
                vesselToEdit.value = null
            },
            onSave = { vessel ->
                if (vesselToEdit.value != null) {
                    Db.updateVessel(vessel)
                } else {
                    Db.addVessel(vessel)
                }
                showAddVesselDialog.value = false
                vesselToEdit.value = null
            }
        )
    }

    if (vesselToDelete.value != null) {
        DeleteVesselDialog(
            vessel = vesselToDelete.value!!,
            onDismiss = { vesselToDelete.value = null },
            onConfirm = {
                Db.deleteVessel(vesselToDelete.value!!.id)
                vesselToDelete.value = null
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vessels") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddVesselDialog.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Vessel")
            }
        },
        bottomBar = {
            AppBottomNavigation(
                currentRoute = "vessels",
                onNavigateToActive = onNavigateToActive,
                onNavigateToIngredients = onNavigateToIngredients,
                onNavigateToVessels = { /* Already on vessels screen */ }
            )
        }
    ) { innerPadding ->
        VesselsList(
            vessels = vessels,
            onEditVessel = { vesselToEdit.value = it },
            onDeleteVessel = { vesselToDelete.value = it },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun VesselsList(
    vessels: List<Vessel>,
    onEditVessel: (Vessel) -> Unit,
    onDeleteVessel: (Vessel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(vessels) { vessel ->
            VesselCard(
                vessel = vessel,
                onEditVessel = { onEditVessel(vessel) },
                onDeleteVessel = { onDeleteVessel(vessel) }
            )
        }
    }
}

@Composable
fun VesselCard(
    vessel: Vessel,
    onEditVessel: () -> Unit,
    onDeleteVessel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vessel.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Capacity: ${vessel.capacity} L", fontSize = 14.sp)
            }

            Row {
                IconButton(onClick = onEditVessel) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Vessel")
                }
                IconButton(onClick = onDeleteVessel) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Vessel")
                }
            }
        }
    }
}

@Composable
fun DeleteVesselDialog(
    vessel: Vessel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Vessel") },
        text = { Text("Are you sure you want to delete ${vessel.name}?") },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddEditVesselDialog(
    vessel: Vessel? = null,
    onDismiss: () -> Unit,
    onSave: (Vessel) -> Unit
) {
    val isEditing = vessel != null
    val title = if (isEditing) "Edit Vessel" else "Add Vessel"

    val vesselName = remember { mutableStateOf(vessel?.name ?: "") }
    val vesselCapacity = remember { mutableStateOf(vessel?.capacity?.toString() ?: "") }
    val isNameValid = remember(vesselName.value) { vesselName.value.isNotBlank() }
    val isCapacityValid = remember(vesselCapacity.value) {
        vesselCapacity.value.toDoubleOrNull() != null || vesselCapacity.value.isEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Vessel Name
                OutlinedTextField(
                    value = vesselName.value,
                    onValueChange = { vesselName.value = it },
                    label = { Text("Vessel Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isNameValid && vesselName.value.isNotEmpty(),
                    supportingText = {
                        if (!isNameValid && vesselName.value.isNotEmpty()) {
                            Text("Name is required")
                        }
                    }
                )

                // Vessel Capacity
                OutlinedTextField(
                    value = vesselCapacity.value,
                    onValueChange = { vesselCapacity.value = it },
                    label = { Text("Capacity (L)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isCapacityValid,
                    supportingText = {
                        if (!isCapacityValid) {
                            Text("Please enter a valid number")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isNameValid && isCapacityValid) {
                        val capacity = vesselCapacity.value.toDoubleOrNull() ?: 0.0
                        val newVessel = Vessel(
                            name = vesselName.value,
                            capacity = capacity
                        )
                        onSave(newVessel)
                    }
                },
                enabled = isNameValid && isCapacityValid
            ) {
                Text(if (isEditing) "Update" else "Add")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun VesselsScreenPreview() {
    val sampleVessels = listOf(
        Vessel("v1", "Glass Jar", 2.0),
        Vessel("v2", "Ceramic Crock", 5.0),
        Vessel("v3", "Plastic Container", 1.5)
    )

    VesselsList(
        vessels = sampleVessels,
        onEditVessel = {},
        onDeleteVessel = {}
    )
}
