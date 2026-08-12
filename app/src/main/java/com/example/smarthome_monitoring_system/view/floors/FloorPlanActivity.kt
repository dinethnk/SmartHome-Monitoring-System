package com.example.smarthome_monitoring_system.view.floors

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.view.camera.CameraActivity
import com.example.smarthome_monitoring_system.view.devices.AddDeviceActivity
import com.example.smarthome_monitoring_system.view.devices.MultiSwitchControlActivity
import com.example.smarthome_monitoring_system.view.devices.OutletControlActivity
import com.example.smarthome_monitoring_system.view.devices.SafetyDeviceActivity
import com.example.smarthome_monitoring_system.view.schedule.LightScheduleActivity
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton

class FloorPlanActivity : AppCompatActivity() {

    private lateinit var floorPlanImage: ImageView
    private lateinit var gridOverlay: GridOverlayView
    private lateinit var markerContainer: FrameLayout

    private lateinit var deviceViewModel: DeviceViewModel

    private var floorId: String = ""

    private var gridRows = 8
    private var gridColumns = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_floor_plan)

        readFloorInformation()
        connectViews()
        setupTopBar()
        setupFloorInformation()
        setupFloorPlanImage()
        setupGrid()
        setupAddDeviceButton()
        setupDeviceObservation()
    }


    // =========================================================
    // Read floor information
    // =========================================================

    private fun readFloorInformation() {

        floorId =
            intent.getStringExtra(
                EXTRA_FLOOR_ID
            ).orEmpty()

        gridRows =
            intent.getIntExtra(
                EXTRA_GRID_ROWS,
                8
            )

        gridColumns =
            intent.getIntExtra(
                EXTRA_GRID_COLUMNS,
                8
            )

        Log.d(
            "FloorPlan",
            "Floor ID: $floorId"
        )

        Log.d(
            "FloorPlan",
            "Grid: ${gridRows}x${gridColumns}"
        )
    }


    // =========================================================
    // Connect views
    // =========================================================

    private fun connectViews() {

        floorPlanImage =
            findViewById(
                R.id.imageFloorPlan
            )

        gridOverlay =
            findViewById(
                R.id.gridOverlay
            )

        markerContainer =
            findViewById(
                R.id.deviceMarkerContainer
            )
    }


    // =========================================================
    // Top bar
    // =========================================================

    private fun setupTopBar() {

        val backButton =
            findViewById<ImageButton>(
                R.id.buttonMenu
            )

        backButton.setImageResource(
            R.drawable.ic_arrow_left
        )

        backButton.contentDescription =
            "Go back"

        backButton.setOnClickListener {
            finish()
        }
    }


    // =========================================================
    // Floor information
    // =========================================================

    private fun setupFloorInformation() {

        val floorName =
            intent.getStringExtra(
                EXTRA_FLOOR_NAME
            ) ?: "Floor Plan"

        findViewById<TextView>(
            R.id.textFloorPlanName
        ).text = floorName

        findViewById<TextView>(
            R.id.textFloorPlanDeviceCount
        ).text = "Loading devices..."
    }


    // =========================================================
    // Floor-plan image
    // =========================================================

    private fun setupFloorPlanImage() {

        val floorPlanUrl =
            intent.getStringExtra(
                EXTRA_FLOOR_PLAN_URL
            ).orEmpty()

        Log.d(
            "FloorPlanImage",
            "URL received: $floorPlanUrl"
        )

        if (floorPlanUrl.isBlank()) {

            Log.d(
                "FloorPlanImage",
                "URL is empty"
            )

            floorPlanImage.setImageResource(
                R.drawable.ic_floor
            )

            return
        }

        Log.d(
            "FloorPlanImage",
            "Loading image..."
        )

        floorPlanImage.load(
            floorPlanUrl
        )
    }


    // =========================================================
    // Grid
    // =========================================================

    private fun setupGrid() {

        gridOverlay.setGridSize(
            gridRows,
            gridColumns
        )
    }


    // =========================================================
    // Add Device button
    // =========================================================

    private fun setupAddDeviceButton() {

        val addDeviceButton =
            findViewById<MaterialButton>(
                R.id.buttonAddDevice
            )

        addDeviceButton.setOnClickListener {

            if (floorId.isBlank()) {

                Toast.makeText(
                    this,
                    "Floor ID is missing",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val floorName =
                intent.getStringExtra(
                    EXTRA_FLOOR_NAME
                ).orEmpty()

            val addDeviceIntent =
                Intent(
                    this,
                    AddDeviceActivity::class.java
                ).apply {

                    putExtra(
                        AddDeviceActivity.EXTRA_FLOOR_ID,
                        floorId
                    )

                    putExtra(
                        AddDeviceActivity.EXTRA_FLOOR_NAME,
                        floorName
                    )

                    putExtra(
                        AddDeviceActivity.EXTRA_GRID_ROWS,
                        gridRows
                    )

                    putExtra(
                        AddDeviceActivity.EXTRA_GRID_COLUMNS,
                        gridColumns
                    )
                }

            startActivity(
                addDeviceIntent
            )
        }
    }


    // =========================================================
    // Observe Firebase devices
    // =========================================================

    private fun setupDeviceObservation() {

        if (floorId.isBlank()) {

            Toast.makeText(
                this,
                "Floor ID is missing",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        deviceViewModel =
            ViewModelProvider(this)[
                DeviceViewModel::class.java
            ]

        deviceViewModel.observeDevicesByFloor(
            floorId
        )

        deviceViewModel.devices.observe(
            this
        ) { devices ->

            Log.d(
                "FloorPlanDevices",
                "Devices received: ${devices.size}"
            )

            updateDeviceCount(
                devices.size
            )

            displayDevices(
                devices
            )
        }

        deviceViewModel.error.observe(
            this
        ) { errorMessage ->

            if (!errorMessage.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // Update device count
    // =========================================================

    private fun updateDeviceCount(
        count: Int
    ) {

        findViewById<TextView>(
            R.id.textFloorPlanDeviceCount
        ).text =
            "$count connected devices"
    }


    // =========================================================
    // Display Firebase devices
    // =========================================================

    private fun displayDevices(
        devices: List<Device>
    ) {

        markerContainer.removeAllViews()

        markerContainer.post {

            for (device in devices) {

                addDeviceMarker(
                    device
                )
            }
        }
    }


    // =========================================================
    // Add device marker
    // =========================================================

    private fun addDeviceMarker(
        device: Device
    ) {

        val markerView =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.item_device_marker,
                    markerContainer,
                    false
                )


        // -----------------------------------------------------
        // Device name
        // -----------------------------------------------------

        markerView.findViewById<TextView>(
            R.id.textDeviceMarkerName
        ).text =
            device.name


        // -----------------------------------------------------
        // Device icon
        // -----------------------------------------------------

        markerView.findViewById<ImageView>(
            R.id.imageDeviceMarker
        ).setImageResource(
            getDeviceIcon(
                device.type
            )
        )


        // -----------------------------------------------------
        // Device status
        // -----------------------------------------------------

        val statusView =
            markerView.findViewById<View>(
                R.id.deviceMarkerStatus
            )

        statusView.backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(
                    getStatusColor(
                        device.status
                    )
                )
            )


        // -----------------------------------------------------
        // Marker size
        // -----------------------------------------------------

        val markerWidth =
            dpToPixels(84)

        val markerHeight =
            dpToPixels(82)


        // -----------------------------------------------------
        // Grid cell size
        // -----------------------------------------------------

        val cellWidth =
            markerContainer.width.toFloat() /
                    gridColumns

        val cellHeight =
            markerContainer.height.toFloat() /
                    gridRows


        // -----------------------------------------------------
        // Marker position
        // -----------------------------------------------------

        val horizontalPosition =
            device.column * cellWidth +
                    cellWidth / 2 -
                    markerWidth / 2

        val verticalPosition =
            device.row * cellHeight +
                    cellHeight / 2 -
                    markerHeight / 2


        val layoutParameters =
            FrameLayout.LayoutParams(
                markerWidth,
                markerHeight
            )


        layoutParameters.leftMargin =
            horizontalPosition
                .toInt()
                .coerceIn(
                    0,
                    markerContainer.width -
                            markerWidth
                )

        layoutParameters.topMargin =
            verticalPosition
                .toInt()
                .coerceIn(
                    0,
                    markerContainer.height -
                            markerHeight
                )


        markerView.layoutParams =
            layoutParameters


        // -----------------------------------------------------
        // Marker click
        // -----------------------------------------------------

        markerView.setOnClickListener {

            openDevice(
                device
            )
        }


        markerContainer.addView(
            markerView
        )
    }


    // =========================================================
    // Device icon
    // =========================================================

    private fun getDeviceIcon(
        type: DeviceType
    ): Int {

        return when (type) {

            DeviceType.LIGHT ->
                R.drawable.ic_light

            DeviceType.OUTLET ->
                R.drawable.ic_devices

            DeviceType.MULTI_SWITCH ->
                R.drawable.ic_light

            DeviceType.SAFETY_DEVICE ->
                R.drawable.ic_iron

            DeviceType.CAMERA ->
                R.drawable.ic_camera
        }
    }


    // =========================================================
    // Device status color
    // =========================================================

    private fun getStatusColor(
        status: DeviceStatus
    ): String {

        return when (status) {

            DeviceStatus.ON ->
                "#00BFA5" // Active Teal

            DeviceStatus.OFF ->
                "#9E9E9E" // Inactive Grey

            DeviceStatus.ERROR ->
                "#E53935" // Error Red

            DeviceStatus.DISCONNECTED ->
                "#FFB300" // Warning Amber
        }
    }


    // =========================================================
    // Open device control screen
    // =========================================================

    private fun openDevice(
        device: Device
    ) {

        when (device.type) {

            DeviceType.LIGHT -> {

                startActivity(
                    Intent(
                        this,
                        LightScheduleActivity::class.java
                    )
                )
            }

            DeviceType.OUTLET -> {

                startActivity(
                    Intent(
                        this,
                        OutletControlActivity::class.java
                    )
                )
            }

            DeviceType.MULTI_SWITCH -> {

                startActivity(
                    Intent(
                        this,
                        MultiSwitchControlActivity::class.java
                    )
                )
            }

            DeviceType.SAFETY_DEVICE -> {

                startActivity(
                    Intent(
                        this,
                        SafetyDeviceActivity::class.java
                    )
                )
            }

            DeviceType.CAMERA -> {

                startActivity(
                    Intent(
                        this,
                        CameraActivity::class.java
                    )
                )
            }
        }
    }


    // =========================================================
    // dp → pixels
    // =========================================================

    private fun dpToPixels(
        dp: Int
    ): Int {

        return (
                dp *
                        resources
                            .displayMetrics
                            .density
                ).toInt()
    }


    // =========================================================
    // Intent constants
    // =========================================================

    companion object {

        const val EXTRA_FLOOR_ID =
            "floor_id"

        const val EXTRA_FLOOR_NAME =
            "floor_name"

        const val EXTRA_GRID_ROWS =
            "grid_rows"

        const val EXTRA_GRID_COLUMNS =
            "grid_columns"

        const val EXTRA_DEVICE_COUNT =
            "device_count"

        const val EXTRA_FLOOR_PLAN_URL =
            "floor_plan_url"
    }
}