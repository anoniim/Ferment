package net.solvetheriddle.fermentlog.domain.model

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

data class Batch(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val status: Status = Status.ACTIVE,
    val phase: BrewingPhase = BrewingPhase.PRIMARY,
    val startDateTimestamp: Long,
    val vessel: Vessel,
    val ingredients: List<IngredientAmount> = emptyList(),
    val parentId: String? = null
) {

    /**
     * Constructor for creating a new batch.
     */
    constructor(
        startDateTimestamp: Long,
        vessel: Vessel,
        ingredients: List<IngredientAmount> = emptyList(),
        parentId: String? = null
    ) : this(
            name = getDefaultName(startDateTimestamp, vessel),
            startDateTimestamp = startDateTimestamp,
            vessel = vessel,
            ingredients = ingredients,
            parentId = parentId
        )

    companion object {
        private fun getDefaultName(startDateTimestamp: Long, vessel: Vessel): String {
            val date = Date(startDateTimestamp)
            val localDate: LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return "${vessel.name} (${localDate.dayOfMonth} ${localDate.month.name})"
        }
    }
}

enum class Status {
    UNDEFINED,
    ACTIVE,
    COMPLETED
}

enum class BrewingPhase {
    UNDEFINED,
    PRIMARY,
    SECONDARY
}
