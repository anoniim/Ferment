package net.solvetheriddle.fermentlog.domain.model

import androidx.annotation.Keep

@Keep
data class Vessel(
    var id: String = "",
    var name: String = "",
    var capacity: Double = 0.0
)
