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
    val startDate: LocalDate,
    val vessel: Vessel,
    val ingredients: List<IngredientAmount> = emptyList(),
    val parentId: String? = null
)

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
