package net.solvetheriddle.fermentlog.domain.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Vessel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val capacity: Double? = null
)
