package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.SafetyRuntime
import com.example.smarthome_monitoring_system.data.model.SafetySettings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SafetyFirebaseDataSource {

    private val safetySettingsReference =
        FirebaseDataSource.safetySettingsReference

    private val safetyRuntimeReference =
        FirebaseDataSource.safetyRuntimeReference


    // =========================================================
    // SAFETY SETTINGS
    // =========================================================

    // ---------------------------------------------------------
    // Observe safety settings for one device
    // ---------------------------------------------------------

    fun observeSafetySettings(
        deviceId: String,
        onSuccess: (SafetySettings?) -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {
            onError("Device ID is required")
            return
        }

        safetySettingsReference
            .child(deviceId)
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            Log.d(
                                "SafetyFirebase",
                                "No safety settings found for device: $deviceId"
                            )

                            onSuccess(null)
                            return
                        }

                        val settings =
                            snapshot.getValue(
                                SafetySettings::class.java
                            )

                        Log.d(
                            "SafetyFirebase",
                            "Loaded safety settings: $settings"
                        )

                        onSuccess(settings)
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
    // Save safety settings
    // ---------------------------------------------------------

    fun saveSafetySettings(
        settings: SafetySettings,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (settings.deviceId.isBlank()) {

            onError(
                "Device ID is required"
            )

            return
        }

        safetySettingsReference
            .child(settings.deviceId)
            .setValue(settings)
            .addOnSuccessListener {

                Log.d(
                    "SafetyFirebase",
                    "Safety settings saved for device: ${settings.deviceId}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to save safety settings"
                )
            }
    }


    // =========================================================
    // SAFETY RUNTIME
    // =========================================================

    // ---------------------------------------------------------
    // Observe runtime information
    // ---------------------------------------------------------

    fun observeSafetyRuntime(
        deviceId: String,
        onSuccess: (SafetyRuntime?) -> Unit,
        onError: (String) -> Unit
    ) {

        if (deviceId.isBlank()) {

            onError(
                "Device ID is required"
            )

            return
        }

        safetyRuntimeReference
            .child(deviceId)
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            Log.d(
                                "SafetyFirebase",
                                "No runtime data found for device: $deviceId"
                            )

                            onSuccess(null)
                            return
                        }

                        val runtime =
                            snapshot.getValue(
                                SafetyRuntime::class.java
                            )

                        Log.d(
                            "SafetyFirebase",
                            "Loaded safety runtime: $runtime"
                        )

                        onSuccess(runtime)
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
    // Save runtime information
    // ---------------------------------------------------------

    fun saveSafetyRuntime(
        runtime: SafetyRuntime,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (runtime.deviceId.isBlank()) {

            onError(
                "Device ID is required"
            )

            return
        }

        safetyRuntimeReference
            .child(runtime.deviceId)
            .setValue(runtime)
            .addOnSuccessListener {

                Log.d(
                    "SafetyFirebase",
                    "Safety runtime saved for device: ${runtime.deviceId}"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to save safety runtime"
                )
            }
    }


    // ---------------------------------------------------------
    // Clear runtime information
    // ---------------------------------------------------------

    fun clearSafetyRuntime(
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

        safetyRuntimeReference
            .child(deviceId)
            .removeValue()
            .addOnSuccessListener {

                Log.d(
                    "SafetyFirebase",
                    "Safety runtime cleared for device: $deviceId"
                )

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Failed to clear safety runtime"
                )
            }
    }
}