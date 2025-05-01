package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.BrewingPhase
import net.solvetheriddle.fermentlog.domain.model.Status
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

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
    fun toDomain(): Batch {
        return Batch(
            id = id,
            name = name,
            status = status,
            phase = phase,
            startDate = Date(startDateTimestamp).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            vessel = vessel.toDomain(),
            ingredients = ingredients.map { it.toDomain() },
            parentId = parentId
        )
    }

    constructor(batch: Batch) : this(
        id = batch.id,
        name = batch.name.ifBlank { getDefaultName(batch.startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), batch.vessel) },
        status = batch.status,
        phase = batch.phase,
        startDateTimestamp = batch.startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        vessel = VesselData(batch.vessel),
        ingredients = batch.ingredients.map { IngredientAmountData(it) },
        parentId = batch.parentId
    )

    companion object {
        private fun getDefaultName(startDateTimestamp: Long, vessel: Vessel): String {
            val date = Date(startDateTimestamp)
            val localDate: LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return "${vessel.name} (${localDate.dayOfMonth} ${localDate.month.name})"
        }
    }
}
