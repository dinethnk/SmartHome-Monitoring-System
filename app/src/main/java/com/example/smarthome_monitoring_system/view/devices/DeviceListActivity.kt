package com.example.smarthome_monitoring_system.view.devices

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.DeviceAdapter
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.view.camera.CameraActivity
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.view.schedule.LightScheduleActivity
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel

class DeviceListActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var floorViewModel: FloorViewModel

    private lateinit var recyclerDevices: RecyclerView
    private lateinit var textTitle: TextView
    private lateinit var textCount: TextView

    private val allFloors = mutableListOf<Floor>()
    private var filterType: String = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        TopBarHelper.setupNotifications(this)

        filterType = intent.getStringExtra(EXTRA_FILTER_TYPE) ?: "ALL"

        connectViews()
        setupTopBar()
        initViewModels()
        observeData()
    }

    private fun connectViews() {
        recyclerDevices = findViewById(R.id.recyclerDevices)
        textTitle = findViewById(R.id.textDeviceListTitle)
        textCount = findViewById(R.id.textDeviceListCount)
        
        textTitle.text = when (filterType) {
            "ON" -> "Devices ON"
            "OFF" -> "Devices OFF"
            else -> "All Devices"
        }
    }

    private fun setupTopBar() {
        val backButton = findViewById<ImageButton>(R.id.buttonMenu)
        backButton.setImageResource(R.drawable.ic_arrow_left)
        backButton.setOnClickListener { finish() }
    }

    private fun initViewModels() {
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]
        floorViewModel = ViewModelProvider(this)[FloorViewModel::class.java]
    }

    private fun observeData() {
        floorViewModel.floors.observe(this) { floors ->
            allFloors.clear()
            allFloors.addAll(floors)
            deviceViewModel.observeAllDevices()
        }

        deviceViewModel.devices.observe(this) { devices ->
            val filteredList = when (filterType) {
                "ON" -> devices.filter { it.status == DeviceStatus.ON }
                "OFF" -> devices.filter { it.status == DeviceStatus.OFF }
                else -> devices
            }
            updateList(filteredList)
        }
    }

    private fun updateList(devices: List<Device>) {
        textCount.text = "${devices.size} devices found"
        
        recyclerDevices.adapter = DeviceAdapter(devices, allFloors) { device, floorName ->
            openDevice(device, floorName)
        }
    }

    private fun openDevice(device: Device, floorName: String) {
        val intent = when (device.type) {
            DeviceType.LIGHT -> Intent(this, LightScheduleActivity::class.java)
            DeviceType.OUTLET -> Intent(this, OutletControlActivity::class.java)
            DeviceType.MULTI_SWITCH -> Intent(this, MultiSwitchControlActivity::class.java)
            DeviceType.SAFETY_DEVICE -> Intent(this, SafetyDeviceActivity::class.java)
            DeviceType.CAMERA -> Intent(this, CameraActivity::class.java)
        }.apply {
            putExtra("device_id", device.id)
            putExtra("floor_name", floorName)
            putExtra("grid_rows", 8)
            putExtra("grid_columns", 8)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_FILTER_TYPE = "filter_type"
    }
}