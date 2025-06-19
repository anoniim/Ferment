package net.solvetheriddle.fermentlog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.solvetheriddle.fermentlog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToIngredients: () -> Unit,
    onNavigateToVessels: () -> Unit,
    onNavigateBack: () -> Unit,
    onSignOut: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PreferenceCategory(title = "Data") {
                Preference(
                    title = "Ingredients",
                    summary = "Manage your list of ingredients",
                    icon = R.drawable.ingredients,
                    onClick = onNavigateToIngredients
                )
                Preference(
                    title = "Vessels",
                    summary = "Manage your list of vessels",
                    icon = R.drawable.vessels,
                    onClick = onNavigateToVessels
                )
            }

            PreferenceCategory(title = "Account") {
                Preference(
                    title = "Log out",
                    summary = "Sign out of your account",
                    icon = R.drawable.logout,
                    onClick = onSignOut
                )
            }
        }
    }
}
