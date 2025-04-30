package net.solvetheriddle.fermentlog.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Vessel
import net.solvetheriddle.fermentlog.data.model.BatchData
import net.solvetheriddle.fermentlog.data.model.IngredientAmountData
import net.solvetheriddle.fermentlog.data.model.IngredientData
import net.solvetheriddle.fermentlog.data.model.VesselData

object Db {
    private const val TAG = "Db"
    private val database = FirebaseDatabase.getInstance()
    private val batchesRef = database.getReference("batches")
    private val vesselsRef = database.getReference("vessels")

    // For preview and testing only
    val sampleVessel = VesselData("v1", "Glass Jar", 2.0)
    val sampleIngredient = IngredientData("i1", "Tea")
    val sampleIngredientAmount = IngredientAmountData(sampleIngredient, "8 spoons")

    fun getBatchesFlow(): Flow<List<BatchData>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val batches = mutableListOf<BatchData>()
                for (batchSnapshot in snapshot.children) {
                    batchSnapshot.getValue<BatchData>()?.let { batch ->
                        batches.add(batch)
                    }
                }
                trySend(batches)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting batches: ${error.message}")
            }
        }

        batchesRef.addValueEventListener(listener)

        awaitClose {
            batchesRef.removeEventListener(listener)
        }
    }

    fun addBatch(batch: Batch) {
        val batchRef = batchesRef.child(batch.id)
        batchRef.setValue(BatchData(batch))
    }

    fun updateBatch(batch: Batch) {
        val batchRef = batchesRef.child(batch.id)
        batchRef.setValue(batch)
    }

    fun deleteBatch(batchId: String) {
        batchesRef.child(batchId).removeValue()
    }

    // Vessel operations
    fun getVesselsFlow(): Flow<List<VesselData>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vessels = mutableListOf<VesselData>()
                for (vesselSnapshot in snapshot.children) {
                    vesselSnapshot.getValue<VesselData>()?.let { vessel ->
                        vessels.add(vessel)
                    }
                }
                trySend(vessels)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting vessels: ${error.message}")
            }
        }

        vesselsRef.addValueEventListener(listener)

        awaitClose {
            vesselsRef.removeEventListener(listener)
        }
    }

    fun addVessel(vessel: Vessel) {
        val vesselData = VesselData(vessel.id, vessel.name, vessel.capacity)
        vesselsRef.child(vesselData.id).setValue(vesselData)
    }

    fun updateVessel(vessel: Vessel) {
        val vesselData = VesselData(vessel.id, vessel.name, vessel.capacity)
        vesselsRef.child(vesselData.id).setValue(vesselData)
    }

    fun deleteVessel(vesselId: String) {
        vesselsRef.child(vesselId).removeValue()
    }
}
