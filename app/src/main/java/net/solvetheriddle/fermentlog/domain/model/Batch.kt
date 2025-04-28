package net.solvetheriddle.fermentlog.domain.model

import java.util.Date

data class Batch(
    val id: String,
    val name: String,
    val status: Status,
    val phase: BrewingPhase,
    val startDate: Date,
    val vessel: Vessel,
    val ingredients: List<Ingredient>,
    val parentId: String? = null // Making parentId nullable as it might not always be present
)

enum class Status {
    ACTIVE,
    COMPLETED
}

enum class BrewingPhase {
    PRIMARY,
    SECONDARY
}