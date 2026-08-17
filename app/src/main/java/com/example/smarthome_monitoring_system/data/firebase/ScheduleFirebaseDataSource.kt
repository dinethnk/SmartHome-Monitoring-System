package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.DeviceSchedule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ScheduleFirebaseDataSource {

    private val schedulesReference =
        FirebaseDataSource.schedulesReference


    // =========================================================
    // Observe schedule for one device
    // =========================================================

    fun observeSchedule(
        deviceId: String,
        onSuccess: (DeviceSchedule?) -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {
            onError("Device ID is required")
            return
        }

        schedulesReference
            .child(deviceId)
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            Log.d(
                                "ScheduleFirebase",
                                "No schedule found for device: $deviceId"
                            )

                            onSuccess(null)
                            return
                        }

                        val schedule =
                            snapshot.getValue(
                                DeviceSchedule::class.java
                            )

                        Log.d(
                            "ScheduleFirebase",
                            "Loaded schedule: $schedule"
                        )

                        onSuccess(schedule)
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        onError(error.message)
                    }
                }
            )
    }


    // =========================================================
    // Save / update schedule
    // =========================================================

    fun saveSchedule(
        schedule: DeviceSchedule,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (schedule.deviceId.isBlank()) {

            onError(
                "Device ID is required"
            )

            return
        }

        schedulesReference
            .child(schedule.deviceId)
            .setValue(schedule)
            .addOnSuccessListener {

                Log.d(
                    "ScheduleFirebase",
                    "Schedule saved for device: ${schedule.deviceId}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to save schedule"
                )
            }
    }


    // =========================================================
    // Delete schedule
    // =========================================================

    fun deleteSchedule(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {

            onError(
                "Device ID is required"
            )

            return
        }

        schedulesReference
            .child(deviceId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(
                    "ScheduleFirebase",
                    "Schedule deleted for device: $deviceId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to delete schedule"
                )
            }
    }

    fun observeAllSchedules(
        onSuccess: (List<DeviceSchedule>) -> Unit,
        onError: (String) -> Unit
    ) {
        schedulesReference.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val schedules = mutableListOf<DeviceSchedule>()
                    for (child in snapshot.children) {
                        val schedule = child.getValue(DeviceSchedule::class.java)
                        if (schedule != null) {
                            schedule.deviceId = child.key ?: ""
                            schedules.add(schedule)
                        }
                    }
                    onSuccess(schedules)
                }
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            }
        )
    }
}