package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.HomeEvent
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class HomeEventViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource = FloorFirebaseDataSource(),
            deviceFirebaseDataSource = DeviceFirebaseDataSource()
        )

    private val _events = MutableLiveData<List<HomeEvent>>()
    val events: LiveData<List<HomeEvent>> get() = _events

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun observeEvents() {
        repository.observeEvents(
            onSuccess = { eventList ->
                _events.postValue(eventList)
            },
            onError = { message ->
                _error.postValue(message)
            }
        )
    }

    fun logEvent(event: HomeEvent) {
        repository.logEvent(
            event = event,
            onSuccess = {},
            onError = { message ->
                _error.postValue(message)
            }
        )
    }
}
