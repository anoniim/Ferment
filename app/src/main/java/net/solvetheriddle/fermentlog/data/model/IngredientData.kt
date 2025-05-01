package net.solvetheriddle.fermentlog.data.model

import androidx.annotation.Keep
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount

@Keep
data class IngredientData(
    val id: String = "undefined",
    val name: String = "undefined",
) {
    fun toDomain(): Ingredient {
        return Ingredient(id = id, name = name)
    }
}

@Keep
data class IngredientAmountData(
    val ingredient: IngredientData = IngredientData(),
    val amount: String = ""
) {
    fun toDomain(): IngredientAmount {
        return IngredientAmount(
            ingredient = ingredient.toDomain(),
            amount = amount
        )
    }

    constructor(ingredientAmount: IngredientAmount) : this(
        ingredient = IngredientData(
            id = ingredientAmount.ingredient.id,
            name = ingredientAmount.ingredient.name
        ),
        amount = ingredientAmount.amount
    )
}
