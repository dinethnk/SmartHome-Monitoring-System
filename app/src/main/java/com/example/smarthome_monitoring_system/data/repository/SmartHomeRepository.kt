package com.example.smarthome_monitoring_system.data.repository

import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.Floor

class SmartHomeRepository(
    private val floorFirebaseDataSource: FloorFirebaseDataSource,
    private val deviceFirebaseDataSource: DeviceFirebaseDataSource
) {

    // =========================================================
    // FLOOR
    // =========================================================

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


    // =========================================================
    // DEVICE
    // =========================================================

    // ---------------------------------------------------------
    // Observe all devices
    // ---------------------------------------------------------

    fun observeDevices(
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {

        deviceFirebaseDataSource.observeDevices(
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Observe devices by floor
    // ---------------------------------------------------------

    fun observeDevicesByFloor(
        floorId: String,
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {

        deviceFirebaseDataSource.observeDevicesByFloor(
            floorId = floorId,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Add device
    // ---------------------------------------------------------

    fun addDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        deviceFirebaseDataSource.addDevice(
            device = device,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Update device
    // ---------------------------------------------------------

    fun updateDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        deviceFirebaseDataSource.updateDevice(
            device = device,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // ---------------------------------------------------------
    // Delete device
    // ---------------------------------------------------------

    fun deleteDevice(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        deviceFirebaseDataSource.deleteDevice(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}