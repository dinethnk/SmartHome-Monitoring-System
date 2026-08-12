package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class DeviceViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource = FloorFirebaseDataSource(),
            deviceFirebaseDataSource = DeviceFirebaseDataSource()
        )


    // =========================================================
    // DEVICES
    // =========================================================

    private val _devices =
        MutableLiveData<List<Device>>()

    val devices: LiveData<List<Device>>
        get() = _devices


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // Observe devices for a specific floor
    // =========================================================

    fun observeDevicesByFloor(
        floorId: String
    ) {

        if (floorId.isBlank()) {

            _devices.postValue(
                emptyList()
            )

            _error.postValue(
                "Floor ID is required"
            )

            return
        }

        repository.observeDevicesByFloor(

            floorId = floorId,

            onSuccess = { devices ->

                _devices.postValue(
                    devices
                )
            },

            onError = { message ->

                _error.postValue(
                    message
                )
            }
        )
    }


    // =========================================================
    // Observe all devices
    // =========================================================

    fun observeAllDevices() {

        repository.observeDevices(

            onSuccess = { devices ->

                _devices.postValue(
                    devices
                )
            },

            onError = { message ->

                _error.postValue(
                    message
                )
            }
        )
    }


    // =========================================================
    // Add device
    // =========================================================

    fun addDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.addDevice(

            device = device,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // Update device
    // =========================================================

    fun updateDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.updateDevice(

            device = device,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // Delete device
    // =========================================================

    fun deleteDevice(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.deleteDevice(

            deviceId = deviceId,

            onSuccess = onSuccess,

            onError = onError
        )
    }
}