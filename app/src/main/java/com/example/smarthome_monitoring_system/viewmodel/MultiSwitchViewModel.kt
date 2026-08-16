package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.SwitchChannel
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class MultiSwitchViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource =
                FloorFirebaseDataSource(),

            deviceFirebaseDataSource =
                DeviceFirebaseDataSource()
        )


    // =========================================================
    // PARENT DEVICE
    // =========================================================

    private val _device =
        MutableLiveData<Device?>()

    val device: LiveData<Device?>
        get() = _device


    // =========================================================
    // CHILD SWITCHES
    // =========================================================

    private val _switchChannels =
        MutableLiveData<List<SwitchChannel>>()

    val switchChannels: LiveData<List<SwitchChannel>>
        get() = _switchChannels


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // OBSERVE PARENT DEVICE
    // =========================================================

    fun observeDevice(
        deviceId: String
    ) {

        if (deviceId.isBlank()) {

            _error.postValue(
                "Multi-switch device ID is required"
            )

            return
        }

        repository.observeDevices(

            onSuccess = { devices ->

                val multiSwitch =
                    devices.firstOrNull {
                        it.id == deviceId
                    }

                if (multiSwitch != null) {

                    _device.postValue(
                        multiSwitch
                    )

                } else {

                    _error.postValue(
                        "Multi-switch device not found"
                    )
                }
            },

            onError = { message ->

                _error.postValue(
                    message
                )
            }
        )
    }


    // =========================================================
    // OBSERVE CHILD SWITCHES
    // =========================================================

    fun observeSwitchChannels(
        deviceId: String
    ) {

        if (deviceId.isBlank()) {

            _error.postValue(
                "Multi-switch device ID is required"
            )

            return
        }

        repository.observeSwitchChannels(

            deviceId = deviceId,

            onSuccess = { switches ->

                _switchChannels.postValue(
                    switches
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
    // ADD CHILD SWITCH
    // =========================================================

    fun addSwitchChannel(
        deviceId: String,
        switchChannel: SwitchChannel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.addSwitchChannel(

            deviceId = deviceId,

            switchChannel = switchChannel,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // UPDATE CHILD SWITCH
    // =========================================================

    fun updateSwitchChannel(
        deviceId: String,
        switchChannel: SwitchChannel,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.updateSwitchChannel(

            deviceId = deviceId,

            switchChannel = switchChannel,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // DELETE CHILD SWITCH
    // =========================================================

    fun deleteSwitchChannel(
        deviceId: String,
        switchId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.deleteSwitchChannel(

            deviceId = deviceId,

            switchId = switchId,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // UPDATE PARENT
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
    // DELETE PARENT
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