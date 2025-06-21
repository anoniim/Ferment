package net.solvetheriddle.fermentlog.ui.screens.settings.vessels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.domain.model.Vessel

class VesselsViewModel(private val db: Db) : ViewModel() {

    private val _vessels = MutableStateFlow<List<Vessel>>(emptyList())
    val vessels: StateFlow<List<Vessel>> = _vessels

    init {
        loadVessels()
    }

    private fun loadVessels() {
        viewModelScope.launch {
            db.getVesselsFlow().collect {
                _vessels.value = it
            }
        }
    }

    fun addVessel(vessel: Vessel) {
        viewModelScope.launch {
            db.addVessel(vessel)
        }
    }

    fun updateVessel(vessel: Vessel) {
        viewModelScope.launch {
            db.updateVessel(vessel)
        }
    }

    fun deleteVessel(vesselId: String) {
        viewModelScope.launch {
            db.deleteVessel(vesselId)
        }
    }
}
