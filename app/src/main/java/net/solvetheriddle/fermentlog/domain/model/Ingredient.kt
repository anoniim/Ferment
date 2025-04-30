package net.solvetheriddle.fermentlog.domain.model

import androidx.annotation.Keep

@Keep
data class Ingredient(
    var id: String = "",
    var name: String = ""
)

@Keep
data class IngredientAmount(
    var ingredient: Ingredient = Ingredient(),
    var amount: String = ""
)
