package net.solvetheriddle.fermentlog.domain.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Ingredient(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)

@Keep
data class IngredientAmount(
    val ingredient: Ingredient,
    val amount: String = ""
)
