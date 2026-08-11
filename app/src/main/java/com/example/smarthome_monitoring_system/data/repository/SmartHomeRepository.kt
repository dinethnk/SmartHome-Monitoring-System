package com.example.smarthome_monitoring_system.data.repository

import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Floor

class SmartHomeRepository(
    private val floorFirebaseDataSource: FloorFirebaseDataSource
) {

    // ---------------------------------------------------------
    // Observe floors
    // ---------------------------------------------------------

    fun observeFloors(
        onSuccess: (List<Floor>) -> Unit,
        onError: (String) -> Unit
    ) {

        floorFirebaseDataSource.observeFloors(
            onSuccess = onSuccess,
            onError = onError
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

        floorFirebaseDataSource.addFloor(
            floor = floor,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Update floor
    // ---------------------------------------------------------

    fun updateFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        floorFirebaseDataSource.updateFloor(
            floor = floor,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Delete floor
    // ---------------------------------------------------------

    fun deleteFloor(
        floorId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        floorFirebaseDataSource.deleteFloor(
            floorId = floorId,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}