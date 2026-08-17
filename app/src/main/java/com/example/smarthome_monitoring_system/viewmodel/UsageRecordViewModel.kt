package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.UsageRecord
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class UsageRecordViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            floorFirebaseDataSource = FloorFirebaseDataSource(),
            deviceFirebaseDataSource = DeviceFirebaseDataSource()
        )

    private val _usageRecords = MutableLiveData<List<UsageRecord>>()
    val usageRecords: LiveData<List<UsageRecord>> get() = _usageRecords

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun observeUsageRecords() {
        repository.observeUsageRecords(
            onSuccess = { records ->
                _usageRecords.postValue(records)
            },
            onError = { message ->
                _error.postValue(message)
            }
        )
    }
}
