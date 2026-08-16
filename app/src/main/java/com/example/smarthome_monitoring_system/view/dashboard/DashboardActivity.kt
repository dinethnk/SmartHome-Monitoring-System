package com.example.smarthome_monitoring_system.view.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.QuickActionAdapter
import com.example.smarthome_monitoring_system.adapter.RecentActivityAdapter
import com.example.smarthome_monitoring_system.adapter.StatCardAdapter
import com.example.smarthome_monitoring_system.data.firebase.FirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.QuickAction
import com.example.smarthome_monitoring_system.data.model.RecentActivity
import com.example.smarthome_monitoring_system.data.model.StatCard
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.view.floors.FloorPlanActivity
import com.example.smarthome_monitoring_system.view.floors.ManageFloorsActivity
import com.example.smarthome_monitoring_system.viewmodel.AlertViewModel
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DashboardActivity : AppCompatActivity() {

    private lateinit var floorViewModel: FloorViewModel
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var alertViewModel: AlertViewModel

    private val floors = mutableListOf<com.example.smarthome_monitoring_system.data.model.Floor>()
    private val devices = mutableListOf<com.example.smarthome_monitoring_system.data.model.Device>()
    private val alerts = mutableListOf<com.example.smarthome_monitoring_system.data.model.Alert>()
    
    private var currentFloorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        // Setup shared top bar notification button.
        TopBarHelper.setupNotifications(this)

        // Initialize ViewModels
        floorViewModel = ViewModelProvider(this)[FloorViewModel::class.java]
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]
        alertViewModel = ViewModelProvider(this)[AlertViewModel::class.java]

        // Setup dashboard sections.
        setupFloorSelector()
        setupHomeOverview()
        setupQuickActions()
        setupRecentActivity()

        // Observe Data
        observeData()
    }

    private fun observeData() {
        floorViewModel.floors.observe(this) { floorList ->
            floors.clear()
            floors.addAll(floorList)
            updateFloorUI()
        }

        deviceViewModel.observeAllDevices()
        deviceViewModel.devices.observe(this) { deviceList ->
            devices.clear()
            devices.addAll(deviceList)
            updateFloorUI()
            updateHomeOverview()
        }

        alertViewModel.observeAlerts()
        alertViewModel.alerts.observe(this) { alertList ->
            alerts.clear()
            alerts.addAll(alertList)
            updateHomeOverview()
        }
    }

    private fun setupFloorSelector() {
        val buttonPrevious = findViewById<MaterialButton>(R.id.buttonPreviousFloor)
        val buttonNext = findViewById<MaterialButton>(R.id.buttonNextFloor)
        val floorCard = findViewById<View>(R.id.floorSelector)

        buttonPrevious.setOnClickListener {
            if (floors.isNotEmpty()) {
                currentFloorIndex = if (currentFloorIndex > 0) currentFloorIndex - 1 else floors.size - 1
                updateFloorUI()
            }
        }

        buttonNext.setOnClickListener {
            if (floors.isNotEmpty()) {
                currentFloorIndex = if (currentFloorIndex < floors.size - 1) currentFloorIndex + 1 else 0
                updateFloorUI()
            }
        }

        floorCard.setOnClickListener {
            if (floors.isNotEmpty()) {
                val selectedFloor = floors[currentFloorIndex]
                val intent = Intent(this, FloorPlanActivity::class.java).apply {
                    putExtra(FloorPlanActivity.EXTRA_FLOOR_ID, selectedFloor.id)
                    putExtra(FloorPlanActivity.EXTRA_FLOOR_NAME, selectedFloor.name)
                    putExtra(FloorPlanActivity.EXTRA_GRID_ROWS, selectedFloor.gridRows)
                    putExtra(FloorPlanActivity.EXTRA_GRID_COLUMNS, selectedFloor.gridColumns)
                    putExtra(FloorPlanActivity.EXTRA_FLOOR_PLAN_URL, selectedFloor.floorPlanUrl)
                }
                startActivity(intent)
            }
        }

        updateFloorUI()
    }

    private fun updateFloorUI() {
        val textFloorName = findViewById<TextView>(R.id.textFloorName)
        val textFloorDeviceCount = findViewById<TextView>(R.id.textFloorDeviceCount)

        if (floors.isEmpty()) {
            textFloorName.text = "No floors added"
            textFloorDeviceCount.text = "Tap to add a floor"
            return
        }

        val currentFloor = floors[currentFloorIndex]
        textFloorName.text = currentFloor.name

        val floorDeviceCount = devices.count { it.floorId == currentFloor.id }
        textFloorDeviceCount.text = "$floorDeviceCount connected devices"
    }

    private fun testFirebaseConnection() {

        FirebaseDataSource.floorsReference
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        Log.d(
                            "FirebaseTest",
                            "Connected Successfully!"
                        )

                        Log.d(
                            "FirebaseTest",
                            snapshot.value.toString()
                        )
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Log.e(
                            "FirebaseTest",
                            error.message
                        )
                    }
                }
            )
    }

    private fun setupHomeOverview() {
        updateHomeOverview()
    }

    private fun updateHomeOverview() {
        val recyclerHomeOverview = findViewById<RecyclerView>(R.id.recyclerHomeOverview)

        val totalDevices = devices.size
        val devicesOn = devices.count { it.status == com.example.smarthome_monitoring_system.data.model.DeviceStatus.ON }
        val devicesOff = devices.count { it.status == com.example.smarthome_monitoring_system.data.model.DeviceStatus.OFF }
        val activeAlerts = alerts.count { !it.read }

        val overviewCards = listOf(
            StatCard(
                title = "Total Devices",
                value = totalDevices.toString(),
                iconResource = R.drawable.ic_devices,
                tintColor = Color.parseColor("#1A237E"), // Indigo
                backgroundColor = Color.parseColor("#E8EAF6")
            ),
            StatCard(
                title = "Devices ON",
                value = devicesOn.toString(),
                iconResource = R.drawable.ic_power,
                tintColor = Color.parseColor("#00BFA5"), // Teal
                backgroundColor = Color.parseColor("#E0F2F1")
            ),
            StatCard(
                title = "Devices OFF",
                value = devicesOff.toString(),
                iconResource = R.drawable.ic_power,
                tintColor = Color.parseColor("#757575"), // Grey
                backgroundColor = Color.parseColor("#F5F5F5")
            ),
            StatCard(
                title = "Active Alerts",
                value = activeAlerts.toString(),
                iconResource = R.drawable.ic_notifications,
                tintColor = Color.parseColor("#E53935"), // Red
                backgroundColor = Color.parseColor("#FFEBEE")
            )
        )

        recyclerHomeOverview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerHomeOverview.adapter = StatCardAdapter(overviewCards)
    }

    private fun setupQuickActions() {

        val recyclerQuickActions =
            findViewById<RecyclerView>(
                R.id.recyclerQuickActions
            )

        val quickActions =
            listOf(

                QuickAction(
                    title = "Manage Floors",
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
            QuickActionAdapter(
                quickActions
            ) { selectedAction ->

                when (selectedAction.title) {

                    "Manage Floors" -> {

                        val intent =
                            Intent(
                                this,
                                ManageFloorsActivity::class.java
                            )

                        startActivity(intent)
                    }

                    "Cameras" -> {

                        val intent =
                            Intent(
                                this,
                                com.example.smarthome_monitoring_system.view.camera.CameraListActivity::class.java
                            )

                        startActivity(intent)
                    }

                    "Schedules" -> {

                        val intent =
                            Intent(
                                this,
                                com.example.smarthome_monitoring_system.view.schedule.ScheduleListActivity::class.java
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
            findViewById<RecyclerView>(
                R.id.recyclerRecentActivity
            )

        val recentActivities =
            listOf(

                RecentActivity(
                    title = "Living Room Light",
                    description = "Device was turned ON",
                    time = "2 min ago",
                    iconResource = R.drawable.ic_light
                ),

                RecentActivity(
                    title = "Safety Device",
                    description =
                        "Iron was automatically turned OFF",
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

        recyclerRecentActivity.isNestedScrollingEnabled =
            false

        recyclerRecentActivity.adapter =
            RecentActivityAdapter(
                recentActivities
            )
    }
}