package com.example.smarthome_monitoring_system

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "DEVICE_TEST"

        private const val DATABASE_URL =
            "https://nexhome-91fc8-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testDeviceSynchronization()
    }

    private fun testDeviceSynchronization() {
        val database = FirebaseDatabase.getInstance(DATABASE_URL)

        val statusReference = database
            .getReference("devices")
            .child("device_001")
            .child("status")

        statusReference.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)

                    Log.d(
                        TAG,
                        "Current device status: $status"
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        TAG,
                        "Reading failed",
                        error.toException()
                    )
                }
            }
        )

        statusReference.setValue("ON")
            .addOnSuccessListener {
                Log.d(TAG, "Device changed to ON successfully")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Device update failed", exception)
            }
    }
}