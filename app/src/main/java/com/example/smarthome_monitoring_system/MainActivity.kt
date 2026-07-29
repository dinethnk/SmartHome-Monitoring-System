package com.example.smarthome_monitoring_system

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "FIREBASE_TEST"

        private const val DATABASE_URL =
            "https://nexhome-91fc8-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testFirebaseConnection()
    }

    private fun testFirebaseConnection() {
        val database = FirebaseDatabase.getInstance(DATABASE_URL)

        val testReference = database
            .getReference("connectionTest")
            .child("androidApp")

        Log.d(
            "FIREBASE_TEST",
            "Writing to: ${testReference}"
        )

        val testData = mapOf(
            "message" to "Android connected successfully",
            "connected" to true,
            "timestamp" to ServerValue.TIMESTAMP
        )

        testReference.setValue(testData)
            .addOnSuccessListener {
                Log.d(
                    "FIREBASE_TEST",
                    "Firebase connection successful"
                )

                testReference.get()
                    .addOnSuccessListener { snapshot ->
                        Log.d(
                            "FIREBASE_TEST",
                            "Read-back value: ${snapshot.value}"
                        )
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "FIREBASE_TEST",
                            "Read-back failed",
                            exception
                        )
                    }
            }
            .addOnFailureListener { exception ->
                Log.e(
                    "FIREBASE_TEST",
                    "Firebase connection failed",
                    exception
                )
            }
    }
}