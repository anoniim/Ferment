package net.solvetheriddle.fermentlog.ui.screens.batches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.time.ZoneId
import java.util.Date

class AddBatchViewModel(private val db: Db) : ViewModel() {

    private val _vessels = MutableStateFlow<List<Vessel>>(emptyList())
    val vessels: StateFlow<List<Vessel>> = _vessels.asStateFlow()

    private val _selectedVessel = MutableStateFlow<Vessel?>(null)
    val selectedVessel: StateFlow<Vessel?> = _selectedVessel.asStateFlow()

    val batchName = MutableStateFlow("")

    private val _batchAdded = MutableStateFlow(false)
    val batchAdded: StateFlow<Boolean> = _batchAdded.asStateFlow()

    init {
        loadVessels()
    }

    private fun loadVessels() {
        viewModelScope.launch {
            db.getVesselsFlow().collect { fetchedVessels ->
                val oldVesselId = _selectedVessel.value?.id
                _vessels.value = fetchedVessels

                if (_selectedVessel.value == null) {
                    _selectedVessel.value = fetchedVessels.firstOrNull()
                } else {
                    // keep selection if it still exists
                    _selectedVessel.value = fetchedVessels.find { it.id == oldVesselId }
                }
            }
        }
    }

    fun onVesselSelected(vessel: Vessel) {
        _selectedVessel.value = vessel
    }

    fun addVessel(vessel: Vessel) {
        viewModelScope.launch {
            db.addVessel(vessel)
        }
    }

    fun addBatch() {
        viewModelScope.launch {
            _selectedVessel.value?.let { vessel ->
                val newBatch = Batch(
                    name = batchName.value,
                    startDate = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    vessel = vessel,
                )
                db.addBatch(newBatch)
                _batchAdded.value = true
            }
        }
    }

    fun onBatchAddedHandled() {
        _batchAdded.value = false
    }
}
