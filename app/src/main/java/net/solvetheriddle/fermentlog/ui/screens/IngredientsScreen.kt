@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import net.solvetheriddle.fermentlog.domain.model.Ingredient

@Composable
fun IngredientsScreen(
    onNavigateBack: () -> Unit
) {
    val ingredients = remember { mutableStateListOf<Ingredient>() }
    val showAddIngredientDialog = remember { mutableStateOf(false) }
    val ingredientToEdit = remember { mutableStateOf<Ingredient?>(null) }
    val ingredientToDelete = remember { mutableStateOf<Ingredient?>(null) }

    // Collect ingredients from Firebase
    LaunchedEffect(Unit) {
        Db.getIngredientsFlow().collect { fetchedIngredients ->
            ingredients.clear()
            ingredients.addAll(fetchedIngredients)
        }
    }

    if (showAddIngredientDialog.value || ingredientToEdit.value != null) {
        AddEditIngredientDialog(
            ingredient = ingredientToEdit.value,
            onDismiss = {
                showAddIngredientDialog.value = false
                ingredientToEdit.value = null
            },
            onSave = { ingredient ->
                if (ingredientToEdit.value != null) {
                    Db.updateIngredient(ingredient)
                } else {
                    Db.addIngredient(ingredient)
                }
                showAddIngredientDialog.value = false
                ingredientToEdit.value = null
            }
        )
    }

    if (ingredientToDelete.value != null) {
        DeleteIngredientDialog(
            ingredient = ingredientToDelete.value!!,
            onDismiss = { ingredientToDelete.value = null },
            onConfirm = {
                Db.deleteIngredient(ingredientToDelete.value!!.id)
                ingredientToDelete.value = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredients") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddIngredientDialog.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Ingredient")
            }
        },
    ) { innerPadding ->
        IngredientsList(
            ingredients = ingredients,
            onEditIngredient = { ingredientToEdit.value = it },
            onDeleteIngredient = { ingredientToDelete.value = it },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun IngredientsList(
    ingredients: List<Ingredient>,
    onEditIngredient: (Ingredient) -> Unit,
    onDeleteIngredient: (Ingredient) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(ingredients) { ingredient ->
            IngredientCard(
                ingredient = ingredient,
                onEditIngredient = { onEditIngredient(ingredient) },
                onDeleteIngredient = { onDeleteIngredient(ingredient) }
            )
        }
    }
}

@Composable
private fun IngredientCard(
    ingredient: Ingredient,
    onEditIngredient: () -> Unit,
    onDeleteIngredient: () -> Unit
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
                Text(text = ingredient.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Row {
                IconButton(onClick = onEditIngredient) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Ingredient")
                }
                IconButton(onClick = onDeleteIngredient) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Ingredient")
                }
            }
        }
    }
}

@Composable
fun DeleteIngredientDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Ingredient") },
        text = { Text("Are you sure you want to delete ${ingredient.name}?") },
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
fun AddEditIngredientDialog(
    ingredient: Ingredient? = null,
    onDismiss: () -> Unit,
    onSave: (Ingredient) -> Unit
) {
    val isEditing = ingredient != null
    val title = if (isEditing) "Edit Ingredient" else "Add Ingredient"

    val ingredientName = remember { mutableStateOf(ingredient?.name ?: "") }
    val isNameValid = remember(ingredientName.value) { ingredientName.value.isNotBlank() }

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
                // Ingredient Name
                OutlinedTextField(
                    value = ingredientName.value,
                    onValueChange = { ingredientName.value = it },
                    label = { Text("Ingredient Name*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isNameValid && ingredientName.value.isNotEmpty(),
                    supportingText = {
                        if (!isNameValid && ingredientName.value.isNotEmpty()) {
                            Text("Name is required")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isNameValid) {
                        val newIngredient = Ingredient(
                            name = ingredientName.value
                        )
                        onSave(newIngredient)
                    }
                },
                enabled = isNameValid
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
private fun IngredientsScreenPreview() {
    IngredientsScreen(onNavigateBack = {})
}