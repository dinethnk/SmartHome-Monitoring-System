package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.SwitchChannel
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

    // =========================================================
// MULTI-SWITCH CHILDREN
// =========================================================

    fun observeSwitchChannels(
        deviceId: String,
        onSuccess: (List<SwitchChannel>) -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {

            onError(
                "Multi-switch device ID is required"
            )

            return
        }

        devicesReference
            .child(deviceId)
            .child("switches")
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val switches =
                            mutableListOf<SwitchChannel>()

                        for (
                        switchSnapshot
                        in snapshot.children
                        ) {

                            val switchChannel =
                                switchSnapshot.getValue(
                                    SwitchChannel::class.java
                                )

                            if (switchChannel != null) {

                                switchChannel.id =
                                    switchSnapshot.key ?: ""

                                switches.add(
                                    switchChannel
                                )
                            }
                        }

                        onSuccess(
                            switches
                        )
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        onError(
                            error.message
                        )
                    }
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

        if (deviceId.isBlank()) {

            onError(
                "Multi-switch device ID is required"
            )

            return
        }

        val switchId =
            devicesReference
                .child(deviceId)
                .child("switches")
                .push()
                .key

        if (switchId == null) {

            onError(
                "Unable to generate switch ID"
            )

            return
        }

        switchChannel.id =
            switchId

        devicesReference
            .child(deviceId)
            .child("switches")
            .child(switchId)
            .setValue(switchChannel)
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Switch added: $switchId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to add switch"
                )
            }
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

        if (deviceId.isBlank()) {

            onError(
                "Multi-switch device ID is required"
            )

            return
        }

        if (switchChannel.id.isBlank()) {

            onError(
                "Switch ID is required for update"
            )

            return
        }

        devicesReference
            .child(deviceId)
            .child("switches")
            .child(switchChannel.id)
            .setValue(switchChannel)
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Switch updated: ${switchChannel.id}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to update switch"
                )
            }
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

        if (deviceId.isBlank()) {

            onError(
                "Multi-switch device ID is required"
            )

            return
        }

        if (switchId.isBlank()) {

            onError(
                "Switch ID is required for deletion"
            )

            return
        }

        devicesReference
            .child(deviceId)
            .child("switches")
            .child(switchId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(
                    "DeviceFirebase",
                    "Switch deleted: $switchId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to delete switch"
                )
            }
    }
}