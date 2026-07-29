package com.example.smarthome_monitoring_system.view.dashboard

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.data.firebase.FirebaseDataSource
import com.example.smarthome_monitoring_system.R

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect this Activity to activity_dashboard.xml
        setContentView(R.layout.activity_dashboard)

        testFirebaseConnection()
    }

    private fun testFirebaseConnection() {
        FirebaseDataSource.devicesReference
            .child("device_001")
            .child("status")
            .setValue("ON")
            .addOnSuccessListener {
                Log.d(
                    "FIREBASE_TEST",
                    "Device status updated successfully"
                )
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "FIREBASE_TEST",
                    "Device status update failed",
                    exception
                )
            }
    }
}