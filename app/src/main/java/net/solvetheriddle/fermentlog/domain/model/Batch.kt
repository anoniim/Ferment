package net.solvetheriddle.fermentlog.domain.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import java.util.Date

@Keep
data class Batch(
    var id: String = "",
    var name: String = "",
    var status: Status = Status.ACTIVE,
    var phase: BrewingPhase = BrewingPhase.PRIMARY,
    var startDateTimestamp: Long = 0L,
    var vessel: Vessel = Vessel(),
    var ingredients: List<IngredientAmount> = emptyList(),
    var parentId: String? = null
) {
    @get:Exclude
    val startDate: Date = Date(startDateTimestamp)

}

enum class Status {
    ACTIVE,
    COMPLETED
}

enum class BrewingPhase {
    PRIMARY,
    SECONDARY
}
