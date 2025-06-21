@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog.ui.screens.batches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.ui.screens.common.AddEditVesselDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddBatchScreen(
    navController: NavController,
    viewModel: AddBatchViewModel = koinViewModel()
) {
    val batchName by viewModel.batchName.collectAsState()
    val vessels by viewModel.vessels.collectAsState()
    val selectedVessel by viewModel.selectedVessel.collectAsState()
    var showAddVesselDialog by remember { mutableStateOf(false) }
    val batchAdded by viewModel.batchAdded.collectAsState()

    if (showAddVesselDialog) {
        AddEditVesselDialog(
            onDismiss = { showAddVesselDialog = false },
            onSave = {
                viewModel.addVessel(it)
                showAddVesselDialog = false
            }
        )
    }

    LaunchedEffect(batchAdded) {
        if (batchAdded) {
            navController.popBackStack()
            viewModel.onBatchAddedHandled()
        }
    }

    Scaffold(
        topBar = { AddNewBatchTopBar(onNavigateBack = { navController.popBackStack() }) },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                BatchName(batchName = batchName, onBatchNameChanged = { viewModel.batchName.value = it })
                Spacer(modifier = Modifier.height(16.dp))
                VesselSelection(vessels, selectedVessel, { viewModel.onVesselSelected(it) }, { showAddVesselDialog = true })
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        bottomBar = {
            AddBatchButton(selectedVessel) { viewModel.addBatch() }
        }
    )
}

@Composable
private fun AddBatchButton(
    selectedVessel: Vessel?,
    onBatchAdded: () -> Unit
) {
    Button(
        onClick = {
            onBatchAdded()
        },
        enabled = selectedVessel != null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text("Add Batch")
    }
}

@Composable
private fun AddNewBatchTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Add New Batch") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun VesselSelection(
    vessels: List<Vessel>,
    selectedVessel: Vessel?,
    onVesselSelected: (Vessel) -> Unit,
    onAddNewVessel: () -> Unit,
) {
    Text("Vessel", style = MaterialTheme.typography.bodyLarge)
    VesselDropdown(selectedVessel, vessels, onVesselSelected, onAddNewVessel)
}

@Composable
private fun VesselDropdown(
    selectedVessel: Vessel?,
    vessels: List<Vessel>,
    onVesselSelected: (Vessel) -> Unit,
    onAddNewVessel: () -> Unit
) {
    var showVesselDropdown by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val text = selectedVessel?.let { "${it.name} (${it.capacity}l)" } ?: "Select a vessel"
            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AddCircle,
                    modifier = Modifier.size(16.dp),
                    contentDescription = "Vessel"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text)
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
                            onVesselSelected(vessel)
                            showVesselDropdown = false
                        }
                    )
                }
                if (vessels.isNotEmpty()) {
                    HorizontalDivider()
                }
                DropdownMenuItem(
                    text = { Text("Add new vessel...") },
                    onClick = {
                        onAddNewVessel()
                        showVesselDropdown = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BatchName(batchName: String, onBatchNameChanged: (String) -> Unit) {
    OutlinedTextField(
        value = batchName,
        onValueChange = onBatchNameChanged,
        label = { Text("Batch name (optional)") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
}


