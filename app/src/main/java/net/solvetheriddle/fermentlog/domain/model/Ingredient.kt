package net.solvetheriddle.fermentlog.domain.model

data class Ingredient(
    val id: String,
    val name: String,
)

data class IngredientAmount(
    val ingredient: Ingredient,
    val amount: String
)