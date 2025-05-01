package net.solvetheriddle.fermentlog.domain.model

import java.util.UUID

data class Ingredient(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
)

data class IngredientAmount(
    val ingredient: Ingredient,
    val amount: String = ""
)
