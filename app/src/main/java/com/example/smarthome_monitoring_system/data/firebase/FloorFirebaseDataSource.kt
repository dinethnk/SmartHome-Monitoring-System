package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.Floor
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FloorFirebaseDataSource {

    private val floorsReference =
        FirebaseDataSource.floorsReference

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
                            floors.map { "${it.id} = ${it.name}" }
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

    fun addFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val floorId =
            floorsReference.push().key

        if (floorId == null) {
            onError("Unable to generate floor ID")
            return
        }

        floor.id = floorId

        floorsReference
            .child(floorId)
            .setValue(floor)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(
                    exception.message
                        ?: "Failed to save floor"
                )
            }
    }
}