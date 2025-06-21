@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import net.solvetheriddle.fermentlog.auth.AuthenticationManager
import net.solvetheriddle.fermentlog.ui.navigation.AppNavigation
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme


class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthenticationManager(this)

        enableEdgeToEdge()
        setContent {
            FermentTheme {
                AppNavigation(authManager)
            }
        }
    }
}
