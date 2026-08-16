package com.example.smarthome_monitoring_system.view.camera

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.view.devices.AddDeviceActivity
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    // =========================================================
    // VIEW MODEL
    // =========================================================

    private lateinit var deviceViewModel: DeviceViewModel


    // =========================================================
    // VIEWS
    // =========================================================

    private lateinit var cameraSwitch: MaterialSwitch
    private lateinit var controlStateText: TextView
    private lateinit var deviceStateText: TextView

    private lateinit var snapshotImage: ImageView
    private lateinit var snapshotTimeText: TextView


    // =========================================================
    // DEVICE INFORMATION
    // =========================================================

    private var deviceId: String = ""

    private var floorName: String = "Floor"

    private var gridRows: Int = 8

    private var gridColumns: Int = 8

    private var currentDevice: Device? = null


    // =========================================================
    // Prevent Firebase updates from triggering
    // the switch listener again.
    // =========================================================

    private var isUpdatingFromFirebase = false


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_camera
        )

        deviceViewModel =
            ViewModelProvider(this)[DeviceViewModel::class.java]

        TopBarHelper.setupNotifications(this)

        readDeviceInformation()
        connectViews()
        setupTopBar()
        setupCameraSwitch()
        setupActionButtons()
        setupRefreshButton()
        setupSnapshotClick()
        observeDevice()
    }


    // =========================================================
    // READ DEVICE INFORMATION
    // =========================================================

    private fun readDeviceInformation() {

        deviceId =
            intent.getStringExtra(
                EXTRA_DEVICE_ID
            ).orEmpty()

        floorName =
            intent.getStringExtra(
                EXTRA_FLOOR_NAME
            ) ?: "Floor"

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

        if (deviceId.isBlank()) {

            Toast.makeText(
                this,
                "Camera ID is missing",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }
    }


    // =========================================================
    // CONNECT VIEWS
    // =========================================================

    private fun connectViews() {

        cameraSwitch =
            findViewById(
                R.id.switchCameraPower
            )

        controlStateText =
            findViewById(
                R.id.textCameraControlState
            )

        deviceStateText =
            findViewById(
                R.id.textDeviceState
            )

        snapshotImage =
            findViewById(
                R.id.imageCameraSnapshot
            )

        snapshotTimeText =
            findViewById(
                R.id.textSnapshotTime
            )
    }


    // =========================================================
    // TOP BAR
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
    // CAMERA SWITCH
    // =========================================================

    private fun setupCameraSwitch() {

        cameraSwitch.setOnCheckedChangeListener {
                _,
                isChecked ->

            if (isUpdatingFromFirebase) {
                return@setOnCheckedChangeListener
            }

            updateCameraStatus(
                isChecked
            )
        }
    }


    // =========================================================
    // ACTION BUTTONS
    // =========================================================

    private fun setupActionButtons() {

        findViewById<MaterialButton>(
            R.id.buttonEditDevice
        ).setOnClickListener {

            onEditClick()
        }

        findViewById<MaterialButton>(
            R.id.buttonDeleteDevice
        ).setOnClickListener {

            onDeleteClick()
        }
    }


    // =========================================================
    // EDIT CAMERA
    // =========================================================

    private fun onEditClick() {

        val device =
            currentDevice ?: return

        val intent =
            Intent(
                this,
                AddDeviceActivity::class.java
            ).apply {

                putExtra(
                    AddDeviceActivity.EXTRA_EDIT_MODE,
                    true
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_ID,
                    device.id
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_NAME,
                    device.name
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_TYPE,
                    device.type.name
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_STATUS,
                    device.status.name
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_ROW,
                    device.row
                )

                putExtra(
                    AddDeviceActivity.EXTRA_DEVICE_COLUMN,
                    device.column
                )

                putExtra(
                    AddDeviceActivity.EXTRA_FLOOR_ID,
                    device.floorId
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

        startActivity(intent)
    }


    // =========================================================
    // DELETE CAMERA
    // =========================================================

    private fun onDeleteClick() {

        val device =
            currentDevice ?: return

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Camera?"
            )

            .setMessage(
                "Are you sure you want to delete \"${device.name}\"?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteCamera(
                    device.id
                )
            }

            .show()
    }


    // =========================================================
    // DELETE FROM FIREBASE
    // =========================================================

    private fun deleteCamera(
        cameraId: String
    ) {

        deviceViewModel.deleteDevice(

            deviceId = cameraId,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Camera deleted",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            onError = { errorMessage ->

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // OBSERVE FIREBASE DEVICE
    // =========================================================

    private fun observeDevice() {

        deviceViewModel.observeAllDevices()

        deviceViewModel.devices.observe(
            this
        ) { devices ->

            val camera =
                devices.firstOrNull {
                    it.id == deviceId
                }

            if (camera == null) {
                return@observe
            }

            currentDevice =
                camera

            updateCameraInformation(
                camera
            )

            updateCameraUI(
                camera.status
            )
        }


        // =====================================================
        // FIREBASE ERROR
        // =====================================================

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
    // CAMERA INFORMATION
    // =========================================================

    private fun updateCameraInformation(
        camera: Device
    ) {

        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(
            R.drawable.ic_camera
        )

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text =
            camera.name

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text =
            "$floorName • Camera"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text =
            if (
                camera.status ==
                DeviceStatus.DISCONNECTED
            ) {
                "Offline"
            } else {
                "Connected"
            }

        findViewById<View>(
            R.id.viewConnectionStatusDot
        ).backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(
                    if (
                        camera.status ==
                        DeviceStatus.DISCONNECTED
                    ) {
                        "#9E9E9E"
                    } else {
                        "#00BFA5"
                    }
                )
            )
    }


    // =========================================================
    // UPDATE FIREBASE CAMERA STATUS
    // =========================================================

    private fun updateCameraStatus(
        isOn: Boolean
    ) {

        val camera =
            currentDevice ?: return

        val newStatus =
            if (isOn) {
                DeviceStatus.ON
            } else {
                DeviceStatus.OFF
            }

        val updatedCamera =
            camera.copy(
                status = newStatus
            )

        cameraSwitch.isEnabled =
            false

        deviceViewModel.updateDevice(

            device = updatedCamera,

            onSuccess = {

                cameraSwitch.isEnabled =
                    true

                Toast.makeText(
                    this,
                    if (isOn) {
                        "Camera turned ON"
                    } else {
                        "Camera turned OFF"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { errorMessage ->

                cameraSwitch.isEnabled =
                    true

                isUpdatingFromFirebase =
                    true

                cameraSwitch.isChecked =
                    camera.status ==
                            DeviceStatus.ON

                isUpdatingFromFirebase =
                    false

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // UPDATE CAMERA UI FROM FIREBASE
    // =========================================================

    private fun updateCameraUI(
        status: DeviceStatus
    ) {

        // =========================================================
        // CAMERA SNAPSHOT
        // =========================================================

        if (status == DeviceStatus.ON) {

            snapshotImage.setImageResource(
                R.drawable.livingroomcam
            )

        } else {

            snapshotImage.setImageResource(
                R.drawable.mock_camera_snapshot
            )
        }


        // =========================================================
        // CAMERA SWITCH
        // =========================================================

        val isOn =
            status == DeviceStatus.ON

        isUpdatingFromFirebase =
            true

        cameraSwitch.isChecked =
            isOn

        cameraSwitch.text =
            when (status) {

                DeviceStatus.ON ->
                    "ON"

                DeviceStatus.OFF ->
                    "OFF"

                DeviceStatus.ERROR ->
                    "ERROR"

                DeviceStatus.DISCONNECTED ->
                    "OFFLINE"
            }

        isUpdatingFromFirebase =
            false


        // =========================================================
        // STATUS UI
        // =========================================================

        when (status) {

            // =====================================================
            // ON
            // =====================================================

            DeviceStatus.ON -> {

                controlStateText.text =
                    "Camera is ON"

                controlStateText.setTextColor(
                    Color.parseColor(
                        "#00BFA5"
                    )
                )

                deviceStateText.text =
                    "ON"

                deviceStateText.setTextColor(
                    Color.parseColor(
                        "#00BFA5"
                    )
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#E0F2F1"
                        )
                    )

                cameraSwitch.isEnabled =
                    true
            }


            // =====================================================
            // OFF
            // =====================================================

            DeviceStatus.OFF -> {

                controlStateText.text =
                    "Camera is OFF"

                controlStateText.setTextColor(
                    Color.parseColor(
                        "#9E9E9E"
                    )
                )

                deviceStateText.text =
                    "OFF"

                deviceStateText.setTextColor(
                    Color.parseColor(
                        "#9E9E9E"
                    )
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#F5F5F5"
                        )
                    )

                cameraSwitch.isEnabled =
                    true
            }


            // =====================================================
            // ERROR
            // =====================================================

            DeviceStatus.ERROR -> {

                controlStateText.text =
                    "Camera has an error"

                controlStateText.setTextColor(
                    Color.parseColor(
                        "#E53935"
                    )
                )

                deviceStateText.text =
                    "ERROR"

                deviceStateText.setTextColor(
                    Color.parseColor(
                        "#E53935"
                    )
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFEBEE"
                        )
                    )

                cameraSwitch.isEnabled =
                    false
            }


            // =====================================================
            // DISCONNECTED
            // =====================================================

            DeviceStatus.DISCONNECTED -> {

                controlStateText.text =
                    "Camera is offline"

                controlStateText.setTextColor(
                    Color.parseColor(
                        "#FFB300"
                    )
                )

                deviceStateText.text =
                    "OFFLINE"

                deviceStateText.setTextColor(
                    Color.parseColor(
                        "#FFB300"
                    )
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFF8E1"
                        )
                    )

                cameraSwitch.isEnabled =
                    false
            }
        }
    }


    // =========================================================
    // SNAPSHOT CLICK → FULLSCREEN
    // =========================================================

    private fun setupSnapshotClick() {

        snapshotImage.setOnClickListener {

            val imageResId =
                if (
                    currentDevice?.status ==
                    DeviceStatus.ON
                ) {
                    R.drawable.livingroomcam
                } else {
                    R.drawable.mock_camera_snapshot
                }

            val intent =
                Intent(
                    this,
                    FullscreenImageActivity::class.java
                ).apply {

                    putExtra(
                        FullscreenImageActivity.EXTRA_IMAGE_RES_ID,
                        imageResId
                    )
                }

            startActivity(intent)
        }
    }


    // =========================================================
    // REFRESH SNAPSHOT
    // =========================================================

    private fun setupRefreshButton() {

        val refreshButton =
            findViewById<MaterialButton>(
                R.id.buttonRefreshSnapshot
            )

        refreshButton.setOnClickListener {

            refreshSnapshot()
        }
    }


    private fun refreshSnapshot() {

        val imageResId =
            if (
                currentDevice?.status ==
                DeviceStatus.ON
            ) {
                R.drawable.livingroomcam
            } else {
                R.drawable.mock_camera_snapshot
            }

        snapshotImage.setImageResource(
            imageResId
        )

        snapshotImage.alpha =
            0.4f

        snapshotImage.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        val timeFormatter =
            SimpleDateFormat(
                "hh:mm:ss a",
                Locale.getDefault()
            )

        val currentTime =
            timeFormatter.format(
                Date()
            )

        snapshotTimeText.text =
            "Last refreshed: $currentTime"
    }


    // =========================================================
    // EXTRAS
    // =========================================================

    companion object {

        const val EXTRA_DEVICE_ID =
            "device_id"

        const val EXTRA_FLOOR_NAME =
            "floor_name"

        const val EXTRA_GRID_ROWS =
            "grid_rows"

        const val EXTRA_GRID_COLUMNS =
            "grid_columns"
    }
}