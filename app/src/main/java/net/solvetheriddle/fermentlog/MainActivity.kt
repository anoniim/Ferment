@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.auth.AuthenticationManager
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme
import net.solvetheriddle.fermentlog.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    private lateinit var authManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthenticationManager(this)

        enableEdgeToEdge()
        setContent {
            FermentTheme {
                val authManager = remember { AuthenticationManager() }

                // Start listening for auth state changes
                LaunchedEffect(Unit) {
                    authManager.startListening()
                }

                val batches by Db.getBatchesFlow().collectAsState(initial = emptyList())
                val activeBatches = batches.filter { it.status == Status.ACTIVE } // TODO Order by phase, then duration

                AppNavigation(authManager, activeBatches = activeBatches)
            }
        }
    }
}
