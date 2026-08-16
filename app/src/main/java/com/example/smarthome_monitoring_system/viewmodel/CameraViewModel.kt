//package com.example.smarthome_monitoring_system.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
//import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
//import com.example.smarthome_monitoring_system.data.model.Device
//import com.example.smarthome_monitoring_system.data.model.DeviceType
//import com.example.smarthome_monitoring_system.data.model.Floor
//import com.example.smarthome_monitoring_system.data.repository.SmartHomeRepository
//
//class CameraViewModel : ViewModel() {
//
//    // =========================================================
//    // REPOSITORY
//    // =========================================================
//
//    private val repository =
//        SmartHomeRepository(
//            floorFirebaseDataSource =
//                FloorFirebaseDataSource(),
//
//            deviceFirebaseDataSource =
//                DeviceFirebaseDataSource()
//        )
//
//
//    // =========================================================
//    // CAMERA
//    // =========================================================
//
//    private val _camera =
//        MutableLiveData<Device?>()
//
//    val camera: LiveData<Device?>
//        get() = _camera
//
//
//    // =========================================================
//    // FLOOR
//    // =========================================================
//
//    private val _floor =
//        MutableLiveData<Floor?>()
//
//    val floor: LiveData<Floor?>
//        get() = _floor
//
//
//    // =========================================================
//    // ERROR
//    // =========================================================
//
//    private val _error =
//        MutableLiveData<String?>()
//
//    val error: LiveData<String?>
//        get() = _error
//
//
//    // =========================================================
//    // OBSERVE CAMERA
//    // =========================================================
//
//    fun observeCamera() {
//
//        repository.observeDevices(
//
//            onSuccess = { devices ->
//
//                // Find the first device whose type is CAMERA.
//                val cameraDevice =
//                    devices.firstOrNull {
//                        it.type == DeviceType.CAMERA
//                    }
//
//                // Update camera LiveData.
//                _camera.postValue(
//                    cameraDevice
//                )
//
//                // If a camera exists,
//                // observe its floor.
//                if (cameraDevice != null) {
//
//                    observeFloor(
//                        cameraDevice.floorId
//                    )
//                }
//            },
//
//            onError = { message ->
//
//                _error.postValue(
//                    message
//                )
//            }
//        )
//    }
//
//
//    // =========================================================
//    // OBSERVE CAMERA FLOOR
//    // =========================================================
//
//    private fun observeFloor(
//        floorId: String
//    ) {
//
//        repository.observeFloors(
//
//            onSuccess = { floors ->
//
//                val cameraFloor =
//                    floors.firstOrNull {
//                        it.id == floorId
//                    }
//
//                _floor.postValue(
//                    cameraFloor
//                )
//            },
//
//            onError = { message ->
//
//                _error.postValue(
//                    message
//                )
//            }
//        )
//    }
//}