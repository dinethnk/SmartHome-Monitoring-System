package com.example.smarthome_monitoring_system.data.firebase

import android.util.Log
import com.example.smarthome_monitoring_system.data.model.Alert
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class AlertFirebaseDataSource {

    private val alertsReference =
        FirebaseDataSource.alertsReference


    // =========================================================
    // OBSERVE ALL ALERTS
    // =========================================================

    fun observeAlerts(
        onSuccess: (List<Alert>) -> Unit,
        onError: (String) -> Unit
    ) {

        alertsReference
            .addValueEventListener(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        val alerts =
                            mutableListOf<Alert>()

                        for (
                        alertSnapshot
                        in snapshot.children
                        ) {

                            val alert =
                                alertSnapshot.getValue(
                                    Alert::class.java
                                )

                            if (alert != null) {

                                alert.id =
                                    alertSnapshot.key ?: ""

                                alerts.add(alert)
                            }
                        }

                        // Newest alerts first.
                        alerts.sortByDescending {
                            it.timestamp
                        }

                        Log.d(
                            "AlertFirebase",
                            "Loaded ${alerts.size} alerts"
                        )

                        onSuccess(alerts)
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Log.e(
                            "AlertFirebase",
                            error.message
                        )

                        onError(
                            error.message
                        )
                    }
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

        alertsReference
            .get()
            .addOnSuccessListener { snapshot ->

                val updates =
                    mutableMapOf<String, Any>()

                for (
                alertSnapshot
                in snapshot.children
                ) {

                    val read =
                        alertSnapshot
                            .child("read")
                            .getValue(
                                Boolean::class.java
                            ) ?: false

                    if (!read) {

                        val alertId =
                            alertSnapshot.key

                        if (alertId != null) {

                            updates[
                                "$alertId/read"
                            ] = true
                        }
                    }
                }


                // -------------------------------------------------
                // Nothing to update
                // -------------------------------------------------

                if (updates.isEmpty()) {

                    Log.d(
                        "AlertFirebase",
                        "No unread alerts to mark as read"
                    )

                    onSuccess()

                    return@addOnSuccessListener
                }


                // -------------------------------------------------
                // Update all unread alerts in one operation
                // -------------------------------------------------

                alertsReference
                    .updateChildren(updates)
                    .addOnSuccessListener {

                        Log.d(
                            "AlertFirebase",
                            "All unread alerts marked as read"
                        )

                        onSuccess()
                    }
                    .addOnFailureListener { exception ->

                        Log.e(
                            "AlertFirebase",
                            "Failed to mark alerts as read",
                            exception
                        )

                        onError(
                            exception.message
                                ?: "Failed to mark alerts as read"
                        )
                    }
            }
            .addOnFailureListener { exception ->

                Log.e(
                    "AlertFirebase",
                    "Failed to read alerts",
                    exception
                )

                onError(
                    exception.message
                        ?: "Failed to load alerts"
                )
            }
    }
}