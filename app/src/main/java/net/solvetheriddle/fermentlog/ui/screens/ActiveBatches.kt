package net.solvetheriddle.fermentlog.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun ActiveBatchesScreen(activeBatches: List<Batch>, modifier: Modifier = Modifier) {
    ActiveBatchesList(
        activeBatches = activeBatches,
        modifier = modifier
    )
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
    Text(text = batch.primaryIngredients.first().ingredient.name, fontSize = 14.sp)
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

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    // Create sample data for preview
    val sampleVessel1 = Vessel("v1", "Poseidon", 2.0)
    val sampleVessel2 = Vessel("v2", "Rákosníček", 2.0)
    val sampleIngredient1 = Ingredient("i1", "Thai Nguyen green")
    val sampleIngredient2 = Ingredient("i2", "Black with spices")
    val sampleIngredient3 = Ingredient("i3", "Ginger")
    val sampleIngredient4 = Ingredient("i4", "Sugar")
    val sampleIngredientAmount1 = IngredientAmount(sampleIngredient1, "8 spoons")
    val sampleIngredientAmount2 = IngredientAmount(sampleIngredient2, "10 spoons")
    val sampleSecondaryIngredients = listOf(
        IngredientAmount(sampleIngredient3, "5cm"),
        IngredientAmount(sampleIngredient4, "10g"),
    )

    val sampleBatches = listOf(
        Batch(
            id = "b2",
            name = "for Eli",
            status = Status.ACTIVE,
            phase = BrewingPhase.SECONDARY,
            startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(2),
            vessel = sampleVessel2,
            primaryIngredients = listOf(sampleIngredientAmount1),
            secondaryIngredients = sampleSecondaryIngredients,
        ),
        Batch(
            id = "b1",
            status = Status.ACTIVE,
            phase = BrewingPhase.PRIMARY,
            startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(34),
            vessel = sampleVessel1,
            primaryIngredients = listOf(sampleIngredientAmount2)
        )
    )
    ActiveBatchesScreen(sampleBatches)
}