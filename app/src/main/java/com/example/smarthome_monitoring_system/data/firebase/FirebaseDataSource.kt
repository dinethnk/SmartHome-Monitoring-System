package com.example.smarthome_monitoring_system.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseDataSource {

    private const val DATABASE_URL =
        "https://smarthomemonitoringsyste-59316-default-rtdb.asia-southeast1.firebasedatabase.app/"

    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance(DATABASE_URL)
    }

    val floorsReference: DatabaseReference
        get() = database.getReference("floors")

    val devicesReference: DatabaseReference
        get() = database.getReference("devices")

    val schedulesReference: DatabaseReference
        get() = database.getReference("schedules")

    val usageRecordsReference: DatabaseReference
        get() = database.getReference("usageRecords")

    val alertsReference: DatabaseReference
        get() = database.getReference("alerts")

    val simulatorEventsReference: DatabaseReference
        get() = database.getReference("simulatorEvents")
}