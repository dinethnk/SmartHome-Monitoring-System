package com.example.smarthome_monitoring_system.view.camera

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.CameraAdapter
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel

class CameraListActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var floorViewModel: FloorViewModel

    private lateinit var recyclerCameras: RecyclerView
    private lateinit var textCameraCount: TextView

    private val allFloors = mutableListOf<Floor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_list)

        TopBarHelper.setupNotifications(this)

        connectViews()
        setupTopBar()
        initViewModels()
        observeData()
    }

    private fun connectViews() {
        recyclerCameras = findViewById(R.id.recyclerCameras)
        textCameraCount = findViewById(R.id.textCameraCount)
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
            val cameras = devices.filter { it.type == DeviceType.CAMERA }
            updateCameraList(cameras)
        }
    }

    private fun updateCameraList(cameras: List<Device>) {
        textCameraCount.text = "${cameras.size} cameras active in your home"
        
        recyclerCameras.adapter = CameraAdapter(cameras, allFloors) { camera, floorName ->
            val intent = Intent(this, CameraActivity::class.java).apply {
                putExtra(CameraActivity.EXTRA_DEVICE_ID, camera.id)
                putExtra(CameraActivity.EXTRA_FLOOR_NAME, floorName)
                // Pass default grid info if needed, though single camera view might not need it for display
                putExtra(CameraActivity.EXTRA_GRID_ROWS, 8)
                putExtra(CameraActivity.EXTRA_GRID_COLUMNS, 8)
            }
            startActivity(intent)
        }
    }
}