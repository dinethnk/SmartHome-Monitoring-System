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
import com.example.smarthome_monitoring_system.viewmodel.HomeEventViewModel
import com.example.smarthome_monitoring_system.viewmodel.UsageRecordViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.GridLayoutManager

class DashboardActivity : AppCompatActivity() {

    private lateinit var floorViewModel: FloorViewModel
    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var usageRecordViewModel: UsageRecordViewModel
    private lateinit var homeEventViewModel: HomeEventViewModel

    private val floors = mutableListOf<com.example.smarthome_monitoring_system.data.model.Floor>()
    private val devices = mutableListOf<com.example.smarthome_monitoring_system.data.model.Device>()
    private val alerts = mutableListOf<com.example.smarthome_monitoring_system.data.model.Alert>()
    private val usageRecords = mutableListOf<com.example.smarthome_monitoring_system.data.model.UsageRecord>()
    private val homeEvents = mutableListOf<com.example.smarthome_monitoring_system.data.model.HomeEvent>()
    
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
        usageRecordViewModel = ViewModelProvider(this)[UsageRecordViewModel::class.java]
        homeEventViewModel = ViewModelProvider(this)[HomeEventViewModel::class.java]

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
            updateRecentActivityUI()
        }

        usageRecordViewModel.observeUsageRecords()
        usageRecordViewModel.usageRecords.observe(this) { recordList ->
            usageRecords.clear()
            usageRecords.addAll(recordList)
            updateRecentActivityUI()
        }

        homeEventViewModel.observeEvents()
        homeEventViewModel.events.observe(this) { eventList ->
            homeEvents.clear()
            homeEvents.addAll(eventList)
            updateRecentActivityUI()
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
                id = "TOTAL",
                title = "Total Devices",
                value = totalDevices.toString(),
                iconResource = R.drawable.ic_devices,
                tintColor = Color.parseColor("#1A237E"), // Indigo
                backgroundColor = Color.parseColor("#E8EAF6")
            ),
            StatCard(
                id = "ON",
                title = "Devices ON",
                value = devicesOn.toString(),
                iconResource = R.drawable.ic_power,
                tintColor = Color.parseColor("#00BFA5"), // Teal
                backgroundColor = Color.parseColor("#E0F2F1")
            ),
            StatCard(
                id = "OFF",
                title = "Devices OFF",
                value = devicesOff.toString(),
                iconResource = R.drawable.ic_power,
                tintColor = Color.parseColor("#757575"), // Grey
                backgroundColor = Color.parseColor("#F5F5F5")
            ),
            StatCard(
                id = "ALERTS",
                title = "Active Alerts",
                value = activeAlerts.toString(),
                iconResource = R.drawable.ic_notifications,
                tintColor = Color.parseColor("#E53935"), // Red
                backgroundColor = Color.parseColor("#FFEBEE")
            )
        )

        recyclerHomeOverview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerHomeOverview.adapter = StatCardAdapter(overviewCards) { clickedCard ->
            when (clickedCard.id) {
                "TOTAL" -> {
                    val intent = Intent(this, com.example.smarthome_monitoring_system.view.devices.DeviceListActivity::class.java).apply {
                        putExtra(com.example.smarthome_monitoring_system.view.devices.DeviceListActivity.EXTRA_FILTER_TYPE, "ALL")
                    }
                    startActivity(intent)
                }
                "ON" -> {
                    val intent = Intent(this, com.example.smarthome_monitoring_system.view.devices.DeviceListActivity::class.java).apply {
                        putExtra(com.example.smarthome_monitoring_system.view.devices.DeviceListActivity.EXTRA_FILTER_TYPE, "ON")
                    }
                    startActivity(intent)
                }
                "OFF" -> {
                    val intent = Intent(this, com.example.smarthome_monitoring_system.view.devices.DeviceListActivity::class.java).apply {
                        putExtra(com.example.smarthome_monitoring_system.view.devices.DeviceListActivity.EXTRA_FILTER_TYPE, "OFF")
                    }
                    startActivity(intent)
                }
                "ALERTS" -> {
                    val intent = Intent(this, com.example.smarthome_monitoring_system.view.alerts.AlertsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
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
            GridLayoutManager(this, 3)

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
        updateRecentActivityUI()
    }

    private fun updateRecentActivityUI() {
        val recyclerRecentActivity = findViewById<RecyclerView>(R.id.recyclerRecentActivity)
        
        val recentActivities = mutableListOf<RecentActivity>()

        // 1. Map Alerts to RecentActivity
        alerts.forEach { alert ->
            recentActivities.add(
                RecentActivity(
                    title = alert.deviceName,
                    description = alert.message,
                    time = getTimeAgo(alert.timestamp),
                    iconResource = getIconForAlert(alert.type),
                    timestamp = alert.timestamp // Need to add timestamp to model for sorting
                )
            )
        }

        // 2. Map UsageRecords to RecentActivity
        usageRecords.forEach { record ->
            recentActivities.add(
                RecentActivity(
                    title = record.deviceName,
                    description = "Device was used for ${record.durationMinutes} minutes",
                    time = getTimeAgo(record.timestamp),
                    iconResource = getIconForDevice(record.deviceName),
                    timestamp = record.timestamp
                )
            )
        }

        // 3. Map HomeEvents to RecentActivity
        homeEvents.forEach { event ->
            recentActivities.add(
                RecentActivity(
                    title = event.deviceName,
                    description = event.message,
                    time = getTimeAgo(event.timestamp),
                    iconResource = getIconForDevice(event.deviceName),
                    timestamp = event.timestamp
                )
            )
        }

        // 4. Sort by timestamp (desc) and take top 10
        val sortedActivities = recentActivities
            .sortedByDescending { it.timestamp }
            .take(10)

        recyclerRecentActivity.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerRecentActivity.isNestedScrollingEnabled = false
        recyclerRecentActivity.adapter = RecentActivityAdapter(sortedActivities)
    }

    private fun getTimeAgo(timestamp: Long): String {
        if (timestamp == 0L) return "Unknown"
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 0 -> "Just now"
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000} min ago"
            diff < 86400_000 -> "${diff / 3600_000} hours ago"
            else -> "${diff / 86400_000} days ago"
        }
    }

    private fun getIconForAlert(type: String): Int {
        return when (type) {
            "SAFETY_CUTOFF" -> R.drawable.ic_iron
            "SECURITY_ALERT" -> R.drawable.ic_camera
            else -> R.drawable.ic_notifications
        }
    }

    private fun getIconForDevice(deviceName: String): Int {
        val name = deviceName.lowercase()
        return when {
            name.contains("light") -> R.drawable.ic_light
            name.contains("outlet") || name.contains("power") -> R.drawable.ic_power
            name.contains("iron") -> R.drawable.ic_iron
            name.contains("camera") -> R.drawable.ic_camera
            else -> R.drawable.ic_devices
        }
    }
}