package com.example.smarthome_monitoring_system.data.repository

import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Floor

class SmartHomeRepository(
    private val floorFirebaseDataSource: FloorFirebaseDataSource
) {

    fun observeFloors(
        onSuccess: (List<Floor>) -> Unit,
        onError: (String) -> Unit
    ) {
        floorFirebaseDataSource.observeFloors(
            onSuccess = onSuccess,
            onError = onError
        )
    }

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
}