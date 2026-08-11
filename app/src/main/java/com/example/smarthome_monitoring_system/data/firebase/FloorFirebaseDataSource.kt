package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.Floor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FloorFirebaseDataSource {

    private val floorsReference =
        FirebaseDataSource.floorsReference


    // ---------------------------------------------------------
    // Observe all floors
    // ---------------------------------------------------------

    fun observeFloors(
        onSuccess: (List<Floor>) -> Unit,
        onError: (String) -> Unit
    ) {

        floorsReference.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    Log.d(
                        "FloorFirebase",
                        "Firebase changed. Number of floors: ${snapshot.childrenCount}"
                    )

                    val floors = mutableListOf<Floor>()

                    for (floorSnapshot in snapshot.children) {

                        val floor =
                            floorSnapshot.getValue(Floor::class.java)

                        if (floor != null) {

                            floor.id =
                                floorSnapshot.key ?: ""

                            floors.add(floor)
                        }
                    }

                    Log.d(
                        "FloorFirebase",
                        "Loaded floors: ${
                            floors.map {
                                "${it.id} = ${it.name}, url=${it.floorPlanUrl}"
                            }
                        }"
                    )

                    onSuccess(floors)
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    onError(error.message)
                }
            }
        )
    }


    // ---------------------------------------------------------
    // Add floor
    // ---------------------------------------------------------

    fun addFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val floorId =
            floorsReference.push().key

        if (floorId == null) {

            onError(
                "Unable to generate floor ID"
            )

            return
        }

        floor.id = floorId

        floorsReference
            .child(floorId)
            .setValue(floor)
            .addOnSuccessListener {

                Log.d(
                    "FloorFirebase",
                    "Floor added: ${floor.name}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to save floor"
                )
            }
    }


    // ---------------------------------------------------------
    // Update floor
    // ---------------------------------------------------------

    fun updateFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (floor.id.isBlank()) {

            onError(
                "Floor ID is missing"
            )

            return
        }

        floor.updatedAt =
            System.currentTimeMillis()

        floorsReference
            .child(floor.id)
            .setValue(floor)
            .addOnSuccessListener {

                Log.d(
                    "FloorFirebase",
                    "Floor updated: ${floor.id}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to update floor"
                )
            }
    }


    // ---------------------------------------------------------
    // Delete floor
    // ---------------------------------------------------------

    fun deleteFloor(
        floorId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (floorId.isBlank()) {

            onError(
                "Floor ID is missing"
            )

            return
        }

        floorsReference
            .child(floorId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(
                    "FloorFirebase",
                    "Floor deleted: $floorId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to delete floor"
                )
            }
    }
}