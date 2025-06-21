package net.solvetheriddle.fermentlog.ui.screens.batches

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import net.solvetheriddle.fermentlog.domain.model.Batch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveBatchesScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToAddBatch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveBatchesViewModel = koinViewModel(),
) {
    val activeBatches by viewModel.activeBatches.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Batches") },
                actions = {
                    IconButton(onClick = { println("test click"); onNavigateToSettings() }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddBatch) {
                Icon(Icons.Filled.Add, contentDescription = "Add Batch")
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        ActiveBatchesList(
            activeBatches = activeBatches,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun ActiveBatchesList(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
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
private fun BatchCard(batch: Batch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle batch click */ },
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BatchInfo(batch)
            BrewDuration(batch)

        }
    }
}

@Composable
private fun BatchInfo(batch: Batch) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row (verticalAlignment = Alignment.Bottom) {
            Text(text = batch.vessel.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (!batch.name.isNullOrBlank()) {
                Text(text = " ${batch.name}", fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        PrimaryIngredient(batch)
        Spacer(modifier = Modifier.height(4.dp))
        SecondaryIngredient(batch)
    }
}

@Composable
private fun PrimaryIngredient(batch: Batch) {
    val firstIngredient = batch.primaryIngredients.firstOrNull()
    if (firstIngredient != null) {
        Text(text = firstIngredient.ingredient.name, fontSize = 14.sp)
    }
}

@Composable
private fun SecondaryIngredient(batch: Batch) {
    if (batch.secondaryIngredients.isNotEmpty()) {
        Row {
            val secondaryIngredients = batch.secondaryIngredients.joinToString(separator = ", ") { it.ingredient.name }
            Text(text = "2nd: $secondaryIngredients",
                maxLines = 1,
                fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrewDuration(batch: Batch) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(text = formatDate(batch.startDate), fontSize = 16.sp) } },
        state = rememberTooltipState()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(100.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(text = "${batch.brewDurationInDays}", fontSize = 48.sp)
            Text(text = "days ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
@ReadOnlyComposable
private fun formatDate(date: LocalDate): String {
    val customFormatter = DateTimeFormatter.ofPattern("dd MMMM", getLocale())
    val formattedDate = date.format(customFormatter)
    return formattedDate
}

@Composable
@ReadOnlyComposable
private fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}