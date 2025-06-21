package net.solvetheriddle.fermentlog.ui.screens.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch

class ActiveBatchesViewModel(private val db: Db) : ViewModel() {

    private val _activeBatches = MutableStateFlow<List<Batch>>(emptyList())
    val activeBatches: StateFlow<List<Batch>> = _activeBatches.asStateFlow()

    init {
        loadActiveBatches()
    }

    private fun loadActiveBatches() {
        viewModelScope.launch {
            db.getBatchesFlow().collect { batches ->
                _activeBatches.value = batches
            }
        }
    }
}
