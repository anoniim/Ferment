package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.Vessel

@Keep
data class VesselData(
    val id: String = "undefined",
    val name: String = "undefined",
    val capacity: Double? = null
) {
    fun toDomain(): Vessel {
        return Vessel(
            id = id,
            name = name,
            capacity = capacity
        )
    }

    constructor(vessel: Vessel) : this(
        id = vessel.id,
        name = vessel.name,
        capacity = vessel.capacity
    )
}
