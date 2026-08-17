package com.example.smarthome_monitoring_system.view.schedule

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.ScheduleAdapter
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceSchedule
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.data.model.ScheduleWithDevice
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel
import com.example.smarthome_monitoring_system.viewmodel.ScheduleViewModel

class ScheduleListActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var floorViewModel: FloorViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel

    private lateinit var recyclerSchedules: RecyclerView
    private lateinit var textScheduleCount: TextView

    private val allFloors = mutableListOf<Floor>()
    private val allDevices = mutableListOf<Device>()
    private val allSchedules = mutableListOf<DeviceSchedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)

        TopBarHelper.setupNotifications(this)

        connectViews()
        setupTopBar()
        initViewModels()
        observeData()
    }

    private fun connectViews() {
        recyclerSchedules = findViewById(R.id.recyclerSchedules)
        textScheduleCount = findViewById(R.id.textScheduleCount)
    }

    private fun setupTopBar() {
        val backButton = findViewById<ImageButton>(R.id.buttonMenu)
        backButton.setImageResource(R.drawable.ic_arrow_left)
        backButton.setOnClickListener { finish() }
    }

    private fun initViewModels() {
        deviceViewModel = ViewModelProvider(this)[DeviceViewModel::class.java]
        floorViewModel = ViewModelProvider(this)[FloorViewModel::class.java]
        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
    }

    private fun observeData() {
        floorViewModel.floors.observe(this) { floors ->
            allFloors.clear()
            allFloors.addAll(floors)
            deviceViewModel.observeAllDevices()
        }

        deviceViewModel.devices.observe(this) { devices ->
            allDevices.clear()
            allDevices.addAll(devices)
            scheduleViewModel.observeAllSchedules()
        }

        scheduleViewModel.schedules.observe(this) { schedules ->
            allSchedules.clear()
            allSchedules.addAll(schedules)
            updateUI()
        }
    }

    private fun updateUI() {
        // Filter for lights and join with schedules
        val scheduleList = mutableListOf<ScheduleWithDevice>()
        
        for (device in allDevices) {
            if (device.type == DeviceType.LIGHT) {
                val schedule = allSchedules.find { it.deviceId == device.id }
                if (schedule != null) {
                    val floor = allFloors.find { it.id == device.floorId }
                    scheduleList.add(
                        ScheduleWithDevice(
                            device = device,
                            schedule = schedule,
                            floorName = floor?.name ?: "Unknown Floor"
                        )
                    )
                }
            }
        }

        textScheduleCount.text = "${scheduleList.size} active light schedules"
        
        recyclerSchedules.adapter = ScheduleAdapter(scheduleList) { item ->
            val intent = Intent(this, LightScheduleActivity::class.java).apply {
                putExtra(LightScheduleActivity.EXTRA_DEVICE_ID, item.device.id)
                putExtra(LightScheduleActivity.EXTRA_FLOOR_NAME, item.floorName)
                putExtra(LightScheduleActivity.EXTRA_GRID_ROWS, 8)
                putExtra(LightScheduleActivity.EXTRA_GRID_COLUMNS, 8)
            }
            startActivity(intent)
        }
    }
}