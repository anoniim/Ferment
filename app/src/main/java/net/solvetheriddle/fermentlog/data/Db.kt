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
import net.solvetheriddle.fermentlog.data.model.BatchData
import net.solvetheriddle.fermentlog.data.model.IngredientData
import net.solvetheriddle.fermentlog.data.model.VesselData
import net.solvetheriddle.fermentlog.domain.model.Batch
import net.solvetheriddle.fermentlog.domain.model.Ingredient
import net.solvetheriddle.fermentlog.domain.model.Vessel

object Db {
    private const val TAG = "Db"
    private val database = FirebaseDatabase.getInstance()
    private val batchesRef = database.getReference("batches")
    private val vesselsRef = database.getReference("vessels")
    private val ingredientsRef = database.getReference("ingredients")

    fun getBatchesFlow(): Flow<List<Batch>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val batches = mutableListOf<Batch>()
                for (batchSnapshot in snapshot.children) {
                    batchSnapshot.getValue<BatchData>()?.let { batch ->
                        batches.add(batch.toDomain())
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
    fun getVesselsFlow(): Flow<List<Vessel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vessels = mutableListOf<Vessel>()
                for (vesselSnapshot in snapshot.children) {
                    vesselSnapshot.getValue<VesselData>()?.let { vesselData ->
                        vessels.add(vesselData.toDomain())
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

    // Ingredient operations
    fun getIngredientsFlow(): Flow<List<Ingredient>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ingredients = mutableListOf<Ingredient>()
                for (ingredientSnapshot in snapshot.children) {
                    ingredientSnapshot.getValue<IngredientData>()?.let { ingredientData ->
                        ingredients.add(ingredientData.toDomain())
                    }
                }
                trySend(ingredients)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error getting ingredients: ${error.message}")
            }
        }

        ingredientsRef.addValueEventListener(listener)

        awaitClose {
            ingredientsRef.removeEventListener(listener)
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        val ingredientData = IngredientData(ingredient.id, ingredient.name)
        ingredientsRef.child(ingredientData.id).setValue(ingredientData)
    }

    fun updateIngredient(ingredient: Ingredient) {
        val ingredientData = IngredientData(ingredient.id, ingredient.name)
        ingredientsRef.child(ingredientData.id).setValue(ingredientData)
    }

    fun deleteIngredient(ingredientId: String) {
        ingredientsRef.child(ingredientId).removeValue()
    }
}
