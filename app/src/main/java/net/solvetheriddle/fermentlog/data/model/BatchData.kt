package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant

@Keep
data class BatchData(
    val id: String = "undefined",
    val name: String? = null,
    val status: Status = Status.UNDEFINED,
    val phase: BrewingPhase = BrewingPhase.UNDEFINED,
    val startDateTimestamp: Long = 0,
    val vesselId: String? = null,
    val ingredients: List<IngredientAmountData> = emptyList(),
    val parentId: String? = null
) {
    fun toDomain(vessel: Vessel): Batch {
        return Batch(
            id = id,
            name = name,
            status = status,
            phase = phase,
            startDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(startDateTimestamp), ZoneId.systemDefault()).toLocalDate(),
            vessel = vessel,
            primaryIngredients = ingredients.map { it.toDomain() },
            parentId = parentId
        )
    }

    constructor(batch: Batch) : this(
        id = batch.id,
        name = batch.name,
        status = batch.status,
        phase = batch.phase,
        startDateTimestamp = batch.startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        vesselId = batch.vessel.id,
        ingredients = batch.primaryIngredients.map { IngredientAmountData(it) },
        parentId = batch.parentId
    )
}
