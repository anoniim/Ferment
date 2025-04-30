package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Status

@Keep
data class BatchData(
    val id: String = "undefined",
    val name: String = "undefined",
    val status: Status = Status.UNDEFINED,
    val phase: BrewingPhase = BrewingPhase.UNDEFINED,
    val startDateTimestamp: Long = 0,
    val vessel: VesselData = VesselData(),
    val ingredients: List<IngredientAmountData> = emptyList(),
    val parentId: String? = null
) {
    constructor(batch: Batch) : this(
        id = batch.id,
        name = batch.name,
        status = batch.status,
        phase = batch.phase,
        startDateTimestamp = batch.startDateTimestamp,
        vessel = VesselData(batch.vessel),
        ingredients = batch.ingredients.map { IngredientAmountData(it) },
        parentId = batch.parentId
    )
}
