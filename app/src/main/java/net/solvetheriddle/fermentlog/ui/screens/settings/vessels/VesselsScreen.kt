@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog.ui.screens.settings.vessels

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.ui.screens.common.AddEditVesselDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun VesselsScreen(
    navController: NavController,
    viewModel: VesselsViewModel = koinViewModel()
) {
    val vessels by viewModel.vessels.collectAsState()
    val showAddVesselDialog = remember { mutableStateOf(false) }
    val vesselToEdit = remember { mutableStateOf<Vessel?>(null) }
    val vesselToDelete = remember { mutableStateOf<Vessel?>(null) }

    if (showAddVesselDialog.value || vesselToEdit.value != null) {
        AddEditVesselDialog(
            vessel = vesselToEdit.value,
            onDismiss = {
                showAddVesselDialog.value = false
                vesselToEdit.value = null
            },
            onSave = { vessel ->
                if (vesselToEdit.value != null) {
                    viewModel.updateVessel(vessel)
                } else {
                    viewModel.addVessel(vessel)
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
                viewModel.deleteVessel(vesselToDelete.value!!.id)
                vesselToDelete.value = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vessels") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddVesselDialog.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Vessel")
            }
        },
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
private fun VesselsList(
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
        items(vessels, key = { it.id }) { vessel ->
            VesselCard(
                vessel = vessel,
                onEditVessel = { onEditVessel(vessel) },
                onDeleteVessel = { onDeleteVessel(vessel) }
            )
        }
    }
}

@Composable
private fun VesselCard(
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
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
