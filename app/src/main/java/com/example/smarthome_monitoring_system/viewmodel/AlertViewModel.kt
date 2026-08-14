package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Alert
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class AlertViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource =
                FloorFirebaseDataSource(),

            deviceFirebaseDataSource =
                DeviceFirebaseDataSource()
        )


    // =========================================================
    // ALERTS
    // =========================================================

    private val _alerts =
        MutableLiveData<List<Alert>>()

    val alerts: LiveData<List<Alert>>
        get() = _alerts


    // =========================================================
    // ERROR
    // =========================================================

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error


    // =========================================================
    // OBSERVE ALERTS
    // =========================================================

    fun observeAlerts() {

        repository.observeAlerts(

            onSuccess = { alerts ->

                _alerts.postValue(
                    alerts
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
    // MARK ALL ALERTS AS READ
    // =========================================================

    fun markAllAlertsAsRead(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.markAllAlertsAsRead(

            onSuccess = onSuccess,

            onError = onError
        )
    }
}