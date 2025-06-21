package net.solvetheriddle.fermentlog.ui.screens.settings.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Ingredient

class IngredientsViewModel(private val db: Db) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            db.getIngredientsFlow().collect {
                _ingredients.value = it
            }
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            db.addIngredient(ingredient)
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            db.updateIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredientId: String) {
        viewModelScope.launch {
            db.deleteIngredient(ingredientId)
        }
    }
}
