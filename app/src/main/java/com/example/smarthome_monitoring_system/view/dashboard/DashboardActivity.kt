package com.example.smarthome_monitoring_system.view.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.StatCardAdapter
import com.example.smarthome_monitoring_system.data.model.StatCard
import android.widget.Toast
import com.example.smarthome_monitoring_system.adapter.QuickActionAdapter
import com.example.smarthome_monitoring_system.data.model.QuickAction
import com.example.smarthome_monitoring_system.adapter.RecentActivityAdapter
import com.example.smarthome_monitoring_system.data.model.RecentActivity
import android.content.Intent
import com.example.smarthome_monitoring_system.view.floors.ManageFloorsActivity
import com.example.smarthome_monitoring_system.view.reports.ReportsActivity
import android.util.Log
import com.example.smarthome_monitoring_system.data.firebase.FirebaseDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

//        testFirebaseConnection()

        setupHomeOverview()
        setupQuickActions()
        setupRecentActivity()
    }

    private fun testFirebaseConnection() {

        FirebaseDataSource.floorsReference
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("FirebaseTest", "Connected Successfully!")

                    Log.d("FirebaseTest", snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.e("FirebaseTest", error.message)
                }
            })
    }

    private fun setupHomeOverview() {
        val recyclerHomeOverview =
            findViewById<RecyclerView>(R.id.recyclerHomeOverview)

        val overviewCards = listOf(
            StatCard(
                title = "Total Devices",
                value = "12",
                iconResource = R.drawable.ic_devices
            ),
            StatCard(
                title = "Devices ON",
                value = "7",
                iconResource = R.drawable.ic_devices
            ),
            StatCard(
                title = "Devices OFF",
                value = "5",
                iconResource = R.drawable.ic_devices
            ),
            StatCard(
                title = "Active Alerts",
                value = "1",
                iconResource = R.drawable.ic_devices
            )
        )

        recyclerHomeOverview.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerHomeOverview.adapter =
            StatCardAdapter(overviewCards)
    }

    private fun setupQuickActions() {
        val recyclerQuickActions =
            findViewById<RecyclerView>(R.id.recyclerQuickActions)

        val quickActions = listOf(
            QuickAction(
                title = "Manage Floors",
                iconResource = R.drawable.ic_devices
            ),
            QuickAction(
                title = "Reports",
                iconResource = R.drawable.ic_devices
            ),
            QuickAction(
                title = "Cameras",
                iconResource = R.drawable.ic_camera
            ),
            QuickAction(
                title = "Schedules",
                iconResource = R.drawable.ic_light
            )
        )

        recyclerQuickActions.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerQuickActions.adapter =
            QuickActionAdapter(quickActions) { selectedAction ->

                when (selectedAction.title) {

                    "Manage Floors" -> {
                        val intent = Intent(
                            this,
                            ManageFloorsActivity::class.java
                        )

                        startActivity(intent)
                    }

                    "Reports" -> {
                        val intent = Intent(
                            this,
                            ReportsActivity::class.java
                        )

                        startActivity(intent)
                    }

                    else -> {
                        Toast.makeText(
                            this,
                            "${selectedAction.title} selected",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun setupRecentActivity() {
        val recyclerRecentActivity =
            findViewById<RecyclerView>(R.id.recyclerRecentActivity)

        val recentActivities = listOf(
            RecentActivity(
                title = "Living Room Light",
                description = "Device was turned ON",
                time = "2 min ago",
                iconResource = R.drawable.ic_light
            ),
            RecentActivity(
                title = "Safety Device",
                description = "Iron was automatically turned OFF",
                time = "15 min ago",
                iconResource = R.drawable.ic_devices
            ),
            RecentActivity(
                title = "Security Camera",
                description = "Movement was detected",
                time = "1 hour ago",
                iconResource = R.drawable.ic_camera
            )
        )

        recyclerRecentActivity.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )

        recyclerRecentActivity.isNestedScrollingEnabled = false

        recyclerRecentActivity.adapter =
            RecentActivityAdapter(recentActivities)
    }
}