package com.example.smarthome_monitoring_system.view.devices

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
import com.example.smarthome_monitoring_system.view.devices.AddDeviceActivity
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class OutletControlActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var outletSwitch: MaterialSwitch
    private lateinit var controlStateText: TextView
    private lateinit var deviceStateText: TextView

    private var deviceId: String = ""
    private var floorName: String = ""
    private var gridRows: Int = 8
    private var gridColumns: Int = 8
    private var currentDevice: Device? = null

    private var isUpdatingFromFirebase = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_outlet_control)

        deviceViewModel =
            ViewModelProvider(this)[DeviceViewModel::class.java]

        readDeviceInformation()
        connectViews()
        setupTopBar()
        setupOutletSwitch()
        setupActionButtons()
        observeDevice()
    }


    // ---------------------------------------------------------
    // Action buttons
    // ---------------------------------------------------------

    private fun setupActionButtons() {

        findViewById<MaterialButton>(R.id.buttonEditDevice).setOnClickListener {
            onEditClick()
        }

        findViewById<MaterialButton>(R.id.buttonDeleteDevice).setOnClickListener {
            onDeleteClick()
        }
    }

    private fun onEditClick() {

        val device = currentDevice ?: return

        val intent = Intent(this, AddDeviceActivity::class.java).apply {
            putExtra(AddDeviceActivity.EXTRA_EDIT_MODE, true)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_ID, device.id)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_NAME, device.name)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_TYPE, device.type.name)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_STATUS, device.status.name)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_ROW, device.row)
            putExtra(AddDeviceActivity.EXTRA_DEVICE_COLUMN, device.column)
            putExtra(AddDeviceActivity.EXTRA_FLOOR_ID, device.floorId)
            putExtra(AddDeviceActivity.EXTRA_FLOOR_NAME, floorName)
            putExtra(AddDeviceActivity.EXTRA_GRID_ROWS, gridRows)
            putExtra(AddDeviceActivity.EXTRA_GRID_COLUMNS, gridColumns)
        }

        startActivity(intent)
    }

    private fun onDeleteClick() {

        val device = currentDevice ?: return

        AlertDialog.Builder(this)
            .setTitle("Delete Device?")
            .setMessage("Are you sure you want to delete \"${device.name}\"?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                deleteDevice(device.id)
            }
            .show()
    }

    private fun deleteDevice(deviceId: String) {

        deviceViewModel.deleteDevice(
            deviceId = deviceId,
            onSuccess = {
                Toast.makeText(this, "Device deleted", Toast.LENGTH_SHORT).show()
                finish()
            },
            onError = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }


    // ---------------------------------------------------------
    // Read selected device
    // ---------------------------------------------------------

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
                "Device ID is missing",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }
    }


    // ---------------------------------------------------------
    // Connect views
    // ---------------------------------------------------------

    private fun connectViews() {

        outletSwitch =
            findViewById(
                R.id.switchOutletPower
            )

        controlStateText =
            findViewById(
                R.id.textOutletControlState
            )

        deviceStateText =
            findViewById(
                R.id.textDeviceState
            )
    }


    // ---------------------------------------------------------
    // Top bar
    // ---------------------------------------------------------

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


    // ---------------------------------------------------------
    // Observe Firebase device
    // ---------------------------------------------------------

    private fun observeDevice() {

        deviceViewModel.observeAllDevices()

        deviceViewModel.devices.observe(
            this
        ) { devices ->

            val device =
                devices.firstOrNull {
                    it.id == deviceId
                }

            if (device == null) {
                return@observe
            }

            currentDevice = device

            updateDeviceInformation(
                device
            )

            updateOutletUI(
                device.status
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


    // ---------------------------------------------------------
    // Device information
    // ---------------------------------------------------------

    private fun updateDeviceInformation(
        device: Device
    ) {

        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(
            R.drawable.ic_power
        )

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text =
            device.name

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text =
            "$floorName • Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text =
            if (device.status == DeviceStatus.DISCONNECTED)
                "Offline"
            else
                "Connected"

        findViewById<View>(
            R.id.viewConnectionStatusDot
        ).backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(
                    if (device.status == DeviceStatus.DISCONNECTED)
                        "#9E9E9E"
                    else
                        "#00BFA5"
                )
            )
    }


    // ---------------------------------------------------------
    // Outlet switch
    // ---------------------------------------------------------

    private fun setupOutletSwitch() {

        outletSwitch.setOnCheckedChangeListener {
                _,
                isChecked ->

            if (isUpdatingFromFirebase) {
                return@setOnCheckedChangeListener
            }

            updateDeviceStatus(
                isChecked
            )
        }
    }


    // ---------------------------------------------------------
    // Update Firebase device status
    // ---------------------------------------------------------

    private fun updateDeviceStatus(
        isOn: Boolean
    ) {

        val device =
            currentDevice ?: return

        val newStatus =
            if (isOn) {
                DeviceStatus.ON
            } else {
                DeviceStatus.OFF
            }

        val updatedDevice =
            device.copy(
                status = newStatus
            )

        outletSwitch.isEnabled = false

        deviceViewModel.updateDevice(

            device = updatedDevice,

            onSuccess = {

                outletSwitch.isEnabled = true

                Toast.makeText(
                    this,
                    if (isOn)
                        "Outlet turned ON"
                    else
                        "Outlet turned OFF",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { errorMessage ->

                outletSwitch.isEnabled = true

                isUpdatingFromFirebase = true

                outletSwitch.isChecked =
                    device.status == DeviceStatus.ON

                isUpdatingFromFirebase = false

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // ---------------------------------------------------------
    // Update UI according to Firebase status
    // ---------------------------------------------------------

    private fun updateOutletUI(
        status: DeviceStatus
    ) {

        val isOn =
            status == DeviceStatus.ON

        isUpdatingFromFirebase = true

        outletSwitch.isChecked =
            isOn

        outletSwitch.text =
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

        isUpdatingFromFirebase = false

        when (status) {

            DeviceStatus.ON -> {

                controlStateText.text =
                    "Outlet is ON"

                controlStateText.setTextColor(
                    Color.parseColor("#00BFA5")
                )

                deviceStateText.text =
                    "ON"

                deviceStateText.setTextColor(
                    Color.parseColor("#00BFA5")
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#E0F2F1")
                    )

                outletSwitch.isEnabled = true
            }

            DeviceStatus.OFF -> {

                controlStateText.text =
                    "Outlet is OFF"

                controlStateText.setTextColor(
                    Color.parseColor("#9E9E9E")
                )

                deviceStateText.text =
                    "OFF"

                deviceStateText.setTextColor(
                    Color.parseColor("#9E9E9E")
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#F5F5F5")
                    )

                outletSwitch.isEnabled = true
            }

            DeviceStatus.ERROR -> {

                controlStateText.text =
                    "Outlet has an error"

                controlStateText.setTextColor(
                    Color.parseColor("#E53935")
                )

                deviceStateText.text =
                    "ERROR"

                deviceStateText.setTextColor(
                    Color.parseColor("#E53935")
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#FFEBEE")
                    )

                outletSwitch.isEnabled = false
            }

            DeviceStatus.DISCONNECTED -> {

                controlStateText.text =
                    "Outlet is offline"

                controlStateText.setTextColor(
                    Color.parseColor("#FFB300")
                )

                deviceStateText.text =
                    "OFFLINE"

                deviceStateText.setTextColor(
                    Color.parseColor("#FFB300")
                )

                deviceStateText.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor("#FFF8E1")
                    )

                outletSwitch.isEnabled = false
            }
        }
    }


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