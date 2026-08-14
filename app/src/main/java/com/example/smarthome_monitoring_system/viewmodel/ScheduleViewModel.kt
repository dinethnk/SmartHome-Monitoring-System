package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.model.DeviceSchedule
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class ScheduleViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource =
                com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource(),

            deviceFirebaseDataSource =
                com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource()
        )


    // =========================================================
    // Current schedule
    // =========================================================

    private val _schedule =
        MutableLiveData<DeviceSchedule?>()

    val schedule: LiveData<DeviceSchedule?>
        get() = _schedule


    // =========================================================
    // Error
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // Observe schedule
    // =========================================================

    fun observeSchedule(
        deviceId: String
    ) {

        repository.observeSchedule(

            deviceId = deviceId,

            onSuccess = { schedule ->

                _schedule.postValue(
                    schedule
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
    // Save schedule
    // =========================================================

    fun saveSchedule(
        schedule: DeviceSchedule,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.saveSchedule(

            schedule = schedule,

            onSuccess = onSuccess,

            onError = onError
        )
    }


    // =========================================================
    // Delete schedule
    // =========================================================

    fun deleteSchedule(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.deleteSchedule(

            deviceId = deviceId,

            onSuccess = onSuccess,

            onError = onError
        )
    }
}