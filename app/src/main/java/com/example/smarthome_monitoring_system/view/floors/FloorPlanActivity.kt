package com.example.smarthome_monitoring_system.view.floors

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.view.camera.CameraActivity
import com.example.smarthome_monitoring_system.view.devices.MultiSwitchControlActivity
import com.example.smarthome_monitoring_system.view.devices.OutletControlActivity
import com.example.smarthome_monitoring_system.view.devices.SafetyDeviceActivity
import com.example.smarthome_monitoring_system.view.schedule.LightScheduleActivity
import com.google.android.material.button.MaterialButton

class FloorPlanActivity : AppCompatActivity() {

    private lateinit var floorPlanImage: ImageView
    private lateinit var gridOverlay: GridOverlayView
    private lateinit var markerContainer: FrameLayout

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
        displayTemporaryDevices()
    }

    private fun readFloorInformation() {

        gridRows = intent.getIntExtra(
            EXTRA_GRID_ROWS,
            8
        )

        gridColumns = intent.getIntExtra(
            EXTRA_GRID_COLUMNS,
            8
        )
    }

    private fun connectViews() {

        floorPlanImage =
            findViewById(R.id.imageFloorPlan)

        gridOverlay =
            findViewById(R.id.gridOverlay)

        markerContainer =
            findViewById(R.id.deviceMarkerContainer)
    }

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

    private fun setupFloorInformation() {

        val floorName =
            intent.getStringExtra(
                EXTRA_FLOOR_NAME
            ) ?: "Floor Plan"

        val deviceCount =
            intent.getIntExtra(
                EXTRA_DEVICE_COUNT,
                0
            )

        findViewById<TextView>(
            R.id.textFloorPlanName
        ).text = floorName

        findViewById<TextView>(
            R.id.textFloorPlanDeviceCount
        ).text =
            "$deviceCount connected devices"
    }

    private fun setupFloorPlanImage() {

        val floorPlanUrl =
            intent.getStringExtra(
                EXTRA_FLOOR_PLAN_URL
            ).orEmpty()

        android.util.Log.d(
            "FloorPlanImage",
            "URL received: $floorPlanUrl"
        )

        if (floorPlanUrl.isBlank()) {

            android.util.Log.d(
                "FloorPlanImage",
                "URL is empty"
            )

            floorPlanImage.setImageResource(
                R.drawable.ic_floor
            )

            return
        }

        android.util.Log.d(
            "FloorPlanImage",
            "Loading image..."
        )

        floorPlanImage.load(floorPlanUrl)
    }

    private fun setupGrid() {

        gridOverlay.setGridSize(
            gridRows,
            gridColumns
        )
    }

    private fun setupAddDeviceButton() {

        val addDeviceButton =
            findViewById<MaterialButton>(
                R.id.buttonAddDevice
            )

        addDeviceButton.setOnClickListener {

            Toast.makeText(
                this,
                "Add Device selected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayTemporaryDevices() {

        markerContainer.post {

            addDeviceMarker(
                name = "Light",
                iconResource = R.drawable.ic_light,
                row = 1,
                column = 2,
                statusColor = "#00A67E"
            )

            addDeviceMarker(
                name = "Camera",
                iconResource = R.drawable.ic_camera,
                row = 3,
                column = 5,
                statusColor = "#00A67E"
            )

            addDeviceMarker(
                name = "Outlet",
                iconResource = R.drawable.ic_devices,
                row = 6,
                column = 3,
                statusColor = "#8A848E"
            )

            addDeviceMarker(
                name = "Multi Switch",
                iconResource = R.drawable.ic_light,
                row = 4,
                column = 1,
                statusColor = "#8A848E"
            )

            addDeviceMarker(
                name = "Iron",
                iconResource = R.drawable.ic_iron,
                row = 6,
                column = 6,
                statusColor = "#8A848E"
            )
        }
    }

    private fun addDeviceMarker(
        name: String,
        iconResource: Int,
        row: Int,
        column: Int,
        statusColor: String
    ) {

        val markerView =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.item_device_marker,
                    markerContainer,
                    false
                )

        markerView.findViewById<TextView>(
            R.id.textDeviceMarkerName
        ).text = name

        markerView.findViewById<ImageView>(
            R.id.imageDeviceMarker
        ).setImageResource(
            iconResource
        )

        val statusView =
            markerView.findViewById<View>(
                R.id.deviceMarkerStatus
            )

        statusView.backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(statusColor)
            )

        val markerWidth =
            dpToPixels(84)

        val markerHeight =
            dpToPixels(82)

        val cellWidth =
            markerContainer.width.toFloat() /
                    gridColumns

        val cellHeight =
            markerContainer.height.toFloat() /
                    gridRows

        val horizontalPosition =
            column * cellWidth +
                    cellWidth / 2 -
                    markerWidth / 2

        val verticalPosition =
            row * cellHeight +
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

        markerView.setOnClickListener {

            if (name == "Light") {

                startActivity(
                    Intent(
                        this,
                        LightScheduleActivity::class.java
                    )
                )

            } else if (name == "Outlet") {

                startActivity(
                    Intent(
                        this,
                        OutletControlActivity::class.java
                    )
                )

            } else if (name == "Multi Switch") {

                startActivity(
                    Intent(
                        this,
                        MultiSwitchControlActivity::class.java
                    )
                )

            } else if (name == "Iron") {

                startActivity(
                    Intent(
                        this,
                        SafetyDeviceActivity::class.java
                    )
                )

            } else if (name == "Camera") {

                startActivity(
                    Intent(
                        this,
                        CameraActivity::class.java
                    )
                )

            } else {

                Toast.makeText(
                    this,
                    "$name selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        markerContainer.addView(
            markerView
        )
    }

    private fun dpToPixels(dp: Int): Int {

        return (
                dp *
                        resources.displayMetrics.density
                ).toInt()
    }

    companion object {

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