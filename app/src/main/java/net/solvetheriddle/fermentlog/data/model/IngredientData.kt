package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount

@Keep
data class IngredientData(
    val id: String = "undefined",
    val name: String = "undefined",
)

@Keep
data class IngredientAmountData(
    val ingredient: IngredientData = IngredientData(),
    val amount: String = ""
) {
    constructor(ingredientAmount: IngredientAmount) : this(
        ingredient = IngredientData(
            id = ingredientAmount.ingredient.id,
            name = ingredientAmount.ingredient.name
        ),
        amount = ingredientAmount.amount
    )
}
