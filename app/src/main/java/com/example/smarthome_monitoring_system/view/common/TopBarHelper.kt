package com.example.smarthome_monitoring_system.view.common

import android.app.Activity
import android.view.View
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.firebase.FirebaseDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object TopBarHelper {

    private var alertsListener: ValueEventListener? = null

    fun setupNotifications(
        activity: Activity
    ) {

        val notificationButton =
            activity.findViewById<View>(
                R.id.buttonNotifications
            )

        val notificationDot =
            activity.findViewById<View>(
                R.id.notificationDot
            )

        if (notificationButton == null ||
            notificationDot == null
        ) {
            return
        }

        notificationDot.visibility =
            View.GONE

        notificationButton.setOnClickListener {

            val intent =
                android.content.Intent(
                    activity,
                    com.example.smarthome_monitoring_system.view.alerts.AlertsActivity::class.java
                )

            activity.startActivity(intent)
        }


        // -----------------------------------------------------
        // Listen for unread alerts
        // -----------------------------------------------------

        alertsListener =
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    var hasUnreadAlerts =
                        false

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

                            hasUnreadAlerts =
                                true

                            break
                        }
                    }

                    notificationDot.visibility =
                        if (hasUnreadAlerts) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {
                    notificationDot.visibility =
                        View.GONE
                }
            }


        FirebaseDataSource
            .alertsReference
            .addValueEventListener(
                alertsListener!!
            )
    }
}