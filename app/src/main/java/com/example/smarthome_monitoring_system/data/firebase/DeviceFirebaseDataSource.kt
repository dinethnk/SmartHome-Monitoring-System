package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.Device
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DeviceFirebaseDataSource {

    private val devicesReference =
        FirebaseDataSource.devicesReference


    // ---------------------------------------------------------
    // Observe all devices
    // ---------------------------------------------------------

    fun observeDevices(
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {

        devicesReference.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    Log.d(
                        "DeviceFirebase",
                        "Firebase changed. Number of devices: ${snapshot.childrenCount}"
                    )

                    val devices =
                        mutableListOf<Device>()

                    for (deviceSnapshot in snapshot.children) {

                        val device =
                            deviceSnapshot.getValue(
                                Device::class.java
                            )

                        if (device != null) {

                            device.id =
                                deviceSnapshot.key ?: ""

                            devices.add(device)
                        }
                    }

                    Log.d(
                        "DeviceFirebase",
                        "Loaded devices: ${
                            devices.map {
                                "${it.id} = ${it.name}, floor=${it.floorId}, status=${it.status}"
                            }
                        }"
                    )

                    onSuccess(devices)
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    onError(error.message)
                }
            }
        )
    }


    // ---------------------------------------------------------
    // Observe devices belonging to one floor
    // ---------------------------------------------------------

    fun observeDevicesByFloor(
        floorId: String,
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {

        devicesReference
            .orderByChild("floorId")
            .equalTo(floorId)
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        Log.d(
                            "DeviceFirebase",
                            "Devices for floor $floorId: ${snapshot.childrenCount}"
                        )

                        val devices =
                            mutableListOf<Device>()

                        for (deviceSnapshot in snapshot.children) {

                            val device =
                                deviceSnapshot.getValue(
                                    Device::class.java
                                )

                            if (device != null) {

                                device.id =
                                    deviceSnapshot.key ?: ""

                                devices.add(device)
                            }
                        }

                        Log.d(
                            "DeviceFirebase",
                            "Loaded floor devices: ${
                                devices.map {
                                    "${it.id} = ${it.name}"
                                }
                            }"
                        )

                        onSuccess(devices)
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        onError(error.message)
                    }
                }
            )
    }


    // ---------------------------------------------------------
    // Add device
    // ---------------------------------------------------------

    fun addDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val deviceId =
            devicesReference.push().key

        if (deviceId == null) {

            onError(
                "Unable to generate device ID"
            )

            return
        }

        device.id = deviceId

        devicesReference
            .child(deviceId)
            .setValue(device)
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Device added: $deviceId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to save device"
                )
            }
    }


    // ---------------------------------------------------------
    // Update device
    // ---------------------------------------------------------

    fun updateDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (device.id.isBlank()) {

            onError(
                "Device ID is required for update"
            )

            return
        }

        devicesReference
            .child(device.id)
            .setValue(device)
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Device updated: ${device.id}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to update device"
                )
            }
    }


    // ---------------------------------------------------------
    // Delete device
    // ---------------------------------------------------------

    fun deleteDevice(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {

            onError(
                "Device ID is required for deletion"
            )

            return
        }

        devicesReference
            .child(deviceId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Device deleted: $deviceId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to delete device"
                )
            }
    }
}