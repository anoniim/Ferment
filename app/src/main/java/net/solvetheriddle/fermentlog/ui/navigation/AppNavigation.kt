package net.solvetheriddle.fermentlog.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppBottomNavigation(
    currentRoute: String = "active",
    onNavigateToActive: () -> Unit = {},
    onNavigateToIngredients: () -> Unit = {},
    onNavigateToVessels: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Refresh, "Active") },
            label = { Text("Active") },
            selected = currentRoute == "active",
            onClick = onNavigateToActive
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, "Ingredients") },
            label = { Text("Ingredients") },
            selected = currentRoute == "ingredients",
            onClick = onNavigateToIngredients
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Check, "Vessels") },
            label = { Text("Vessels") },
            selected = currentRoute == "vessels",
            onClick = onNavigateToVessels
        )
    }
}