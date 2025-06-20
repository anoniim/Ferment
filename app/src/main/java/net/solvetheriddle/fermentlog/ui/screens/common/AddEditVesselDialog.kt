package net.solvetheriddle.fermentlog.ui.screens.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.solvetheriddle.fermentlog.domain.model.Vessel

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
                        val newVessel = if (isEditing) {
                            Vessel(
                                id = vessel!!.id, // Keep original id when editing
                                name = vesselName.value,
                                capacity = capacity
                            )
                        } else {
                            Vessel(
                                name = vesselName.value,
                                capacity = capacity
                            )
                        }
                        onSave(newVessel)
                    }
                },
                enabled = isNameValid && isCapacityValid
            ) {
                Text(if (isEditing) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
