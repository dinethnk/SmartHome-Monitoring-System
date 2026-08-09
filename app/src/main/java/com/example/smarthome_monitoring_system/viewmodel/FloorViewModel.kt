package com.example.smarthome_monitoring_system.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository

class FloorViewModel : ViewModel() {

    private val repository =
        SmartHomeRepository(
            FloorFirebaseDataSource()
        )

    private val _floors =
        MutableLiveData<List<Floor>>()

    val floors: LiveData<List<Floor>>
        get() = _floors

    private val _error =
        MutableLiveData<String?>()

    val error: LiveData<String?>
        get() = _error

    init {
        observeFloors()
    }

    private fun observeFloors() {

        repository.observeFloors(

            onSuccess = { floors ->
                _floors.postValue(floors)
            },

            onError = { message ->
                _error.postValue(message)
            }
        )
    }

    fun addFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        repository.addFloor(
            floor = floor,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}