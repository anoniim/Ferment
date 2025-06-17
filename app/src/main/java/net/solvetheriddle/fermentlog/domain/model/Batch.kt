package net.solvetheriddle.fermentlog.domain.model

import android.os.Build
import java.time.LocalDate
import java.util.UUID

data class Batch(
    val id: String = UUID.randomUUID().toString(),
    val name: String? = null,
    val status: Status = Status.ACTIVE,
    val phase: BrewingPhase = BrewingPhase.PRIMARY,
    val startDate: LocalDate,
    val vessel: Vessel,
    val primaryIngredients: List<IngredientAmount> = emptyList(),
    val secondaryIngredients: List<IngredientAmount> = emptyList(),
    val parentId: String? = null
) {
    val brewDurationInDays: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startDate.datesUntil(LocalDate.now()).count().toInt()
        } else {
            TODO("VERSION.SDK_INT < UPSIDE_DOWN_CAKE") // Increase min SDK version and REMOVE THIS CODE
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
