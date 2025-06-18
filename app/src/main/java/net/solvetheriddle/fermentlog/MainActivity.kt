@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.ui.navigation.AppNavigation
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FermentTheme {
                val activeBatches = remember { mutableStateListOf<Batch>() }

                // Collect batches from Firebase
                LaunchedEffect(Unit) {
                    Db.getBatchesFlow().collect { batches ->
                        activeBatches.clear()
                        activeBatches.addAll(batches.filter { it.status == Status.ACTIVE }) // TODO Order by phase, then duration
                    }
                }

                AppNavigation(activeBatches = activeBatches)
            }
        }
    }
}

