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
            FloorFirebaseDataSource(),
            DeviceFirebaseDataSource()
        )


    // ---------------------------------------------------------
    // All devices
    // ---------------------------------------------------------

    private val _devices =
        MutableLiveData<List<Device>>()

    val devices: LiveData<List<Device>>
        get() = _devices


    // ---------------------------------------------------------
    // Error
    // ---------------------------------------------------------

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // ---------------------------------------------------------
    // Observe all devices
    // ---------------------------------------------------------

    init {
        observeDevices()
    }

    private fun observeDevices() {

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


    // ---------------------------------------------------------
    // Observe devices on a specific floor
    // ---------------------------------------------------------

    fun observeDevicesByFloor(
        floorId: String
    ): LiveData<List<Device>> {

        val floorDevices =
            MutableLiveData<List<Device>>()

        repository.observeDevicesByFloor(

            floorId = floorId,

            onSuccess = { devices ->

                floorDevices.postValue(
                    devices
                )
            },

            onError = { message ->

                _error.postValue(
                    message
                )
            }
        )

        return floorDevices
    }


    // ---------------------------------------------------------
    // Add device
    // ---------------------------------------------------------

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


    // ---------------------------------------------------------
    // Update device
    // ---------------------------------------------------------

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


    // ---------------------------------------------------------
    // Delete device
    // ---------------------------------------------------------

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