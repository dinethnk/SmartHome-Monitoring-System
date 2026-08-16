package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.SafetyRuntime
import com.example.smarthome_monitoring_system.data.model.SafetySettings
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class SafetyViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource = FloorFirebaseDataSource(),
            deviceFirebaseDataSource = DeviceFirebaseDataSource()
        )


    // =========================================================
    // SAFETY SETTINGS
    // =========================================================

    private val _safetySettings =
        MutableLiveData<SafetySettings?>()

    val safetySettings: LiveData<SafetySettings?>
        get() = _safetySettings


    // =========================================================
    // SAFETY RUNTIME
    // =========================================================

    private val _safetyRuntime =
        MutableLiveData<SafetyRuntime?>()

    val safetyRuntime: LiveData<SafetyRuntime?>
        get() = _safetyRuntime


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // OBSERVE SAFETY SETTINGS
    // =========================================================

    fun observeSafetySettings(
        deviceId: String
    ) {

        repository.observeSafetySettings(

            deviceId = deviceId,

            onSuccess = { settings ->

                _safetySettings.postValue(
                    settings
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
    // SAVE SAFETY SETTINGS
    // =========================================================

    fun saveSafetySettings(
        settings: SafetySettings,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.saveSafetySettings(

            settings = settings,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // OBSERVE SAFETY RUNTIME
    // =========================================================

    fun observeSafetyRuntime(
        deviceId: String
    ) {

        repository.observeSafetyRuntime(

            deviceId = deviceId,

            onSuccess = { runtime ->

                _safetyRuntime.postValue(
                    runtime
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
    // SAVE SAFETY RUNTIME
    // =========================================================

    fun saveSafetyRuntime(
        runtime: SafetyRuntime,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.saveSafetyRuntime(

            runtime = runtime,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // CLEAR SAFETY RUNTIME
    // =========================================================

    fun clearSafetyRuntime(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.clearSafetyRuntime(

            deviceId = deviceId,

            onSuccess = onSuccess,

            onError = onError
        )
    }
}