@file:OptIn(ExperimentalMaterial3Api::class)

package net.solvetheriddle.fermentlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FermentTheme {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Active Batches") }) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /* Handle add new batch */ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Batch")
                        }
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Refresh, "Active") },
                                label = { Text("Active") },
                                selected = true,
                                onClick = { /* Handle navigation to active */ }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Check, "Completed") },
                                label = { Text("Completed") },
                                selected = false,
                                onClick = { /* Handle navigation to completed */ }
                            )
                        }
                    }
                ) { innerPadding -> // Correct: contentPadding is now innerPadding
                    MainScreen(
                        activeBatches = Db.sampleActiveBatches,
                        modifier = Modifier.padding(innerPadding) // Correct: Apply padding here
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
    ActiveBatchesList(
        activeBatches = activeBatches,
        modifier = modifier
    )
}

@Composable
fun ActiveBatchesList(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(activeBatches) { batch ->
            BatchCard(batch = batch)
        }
    }
}

@Composable
fun BatchCard(batch: Batch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle batch click */ },
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = batch.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Phase: ${batch.phase}", fontSize = 14.sp)
            Text(text = "Vessel: ${batch.vessel.name}", fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Started: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = formatDate(batch.startDate), fontSize = 14.sp)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", getLocale())
    return formatter.format(date)
}

@Composable
@ReadOnlyComposable
fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(Db.sampleActiveBatches)
}