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
import net.solvetheriddle.fermentlog.data.FirebaseTypeConverters.dateToTimestamp
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.IngredientAmount
import net.solvetheriddle.fermentlog.domain.model.Vessel
import java.util.UUID

object Db {
    private const val TAG = "Db"
    private val database = FirebaseDatabase.getInstance()
    private val batchesRef = database.getReference("batches")

    // For preview and testing only
    val sampleVessel = Vessel("v1", "Glass Jar", 2.0)
    val sampleIngredient = Ingredient("i1", "Tea")
    val sampleIngredientAmount = IngredientAmount(sampleIngredient, "8 spoons")

    fun getBatchesFlow(): Flow<List<Batch>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val batches = mutableListOf<Batch>()
                for (batchSnapshot in snapshot.children) {
                    batchSnapshot.getValue<Batch>()?.let { batch ->
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
        val id = batch.id.ifEmpty { UUID.randomUUID().toString() }
        val batchWithId = if (batch.id.isEmpty()) batch.copy(id = id) else batch

        // Store the batch with proper date handling
        val batchRef = batchesRef.child(id)
        batchRef.setValue(batchWithId)

        // Handle date serialization separately
        batchRef.child("startDateTimestamp").setValue(dateToTimestamp(batchWithId.startDate))
    }

    fun updateBatch(batch: Batch) {
        val batchRef = batchesRef.child(batch.id)
        batchRef.setValue(batch)

        // Handle date serialization separately
        batchRef.child("startDateTimestamp").setValue(dateToTimestamp(batch.startDate))
    }

    fun deleteBatch(batchId: String) {
        batchesRef.child(batchId).removeValue()
    }
}
