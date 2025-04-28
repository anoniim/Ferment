package net.solvetheriddle.fermentlog.data

import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.util.Date

object Db {
    // Sample data for preview and testing
    val sampleVessel = Vessel("v1", "Glass Jar", 2.0)
    val sampleIngredient = Ingredient("i1", "Tea", "20g")
    val sampleActiveBatches = listOf(
        Batch(
            id = "b1",
            name = "Kombucha Batch 1",
            status = Status.ACTIVE,
            phase = BrewingPhase.PRIMARY,
            startDate = Date(),
            vessel = sampleVessel,
            ingredients = listOf(sampleIngredient)
        ),
        Batch(
            id = "b2",
            name = "Kombucha Batch 2",
            status = Status.ACTIVE,
            phase = BrewingPhase.SECONDARY,
            startDate = Date(),
            vessel = sampleVessel,
            ingredients = listOf(sampleIngredient)
        ),
        Batch(
            id = "b3",
            name = "Kombucha Batch 3",
            status = Status.ACTIVE,
            phase = BrewingPhase.PRIMARY,
            startDate = Date(),
            vessel = sampleVessel,
            ingredients = listOf(sampleIngredient)
        )
    )
}