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
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class SafetyDeviceActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var powerSwitch: MaterialSwitch
    private lateinit var powerStateText: TextView
    private lateinit var deviceStateText: TextView
    private lateinit var durationInput: TextInputEditText

    private var deviceId: String = ""
    private var floorName: String = ""
    private var gridRows: Int = 8
    private var gridColumns: Int = 8
    private var currentDevice: Device? = null

    private var isUpdatingFromFirebase = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_safety_device)

        deviceViewModel =
            ViewModelProvider(this)[DeviceViewModel::class.java]

        readIntentInformation()
        connectViews()
        setupTopBar()
        setupActionButtons()
        setupPowerSwitch()
        setupSaveDurationButton()
        observeDevice()
    }

    private fun readIntentInformation() {
        deviceId = intent.getStringExtra(EXTRA_DEVICE_ID).orEmpty()
        floorName = intent.getStringExtra(EXTRA_FLOOR_NAME) ?: "Floor"
        gridRows = intent.getIntExtra(EXTRA_GRID_ROWS, 8)
        gridColumns = intent.getIntExtra(EXTRA_GRID_COLUMNS, 8)

        if (deviceId.isBlank()) {
            Toast.makeText(this, "Device ID is missing", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun connectViews() {
        powerSwitch = findViewById(R.id.switchSafetyPower)
        powerStateText = findViewById(R.id.textSafetyPowerState)
        deviceStateText = findViewById(R.id.textDeviceState)
        durationInput = findViewById(R.id.editMaximumDuration)
    }

    private fun setupTopBar() {
        val backButton = findViewById<ImageButton>(R.id.buttonMenu)
        backButton.setImageResource(R.drawable.ic_arrow_left)
        backButton.contentDescription = "Go back"
        backButton.setOnClickListener { finish() }
    }

    private fun setupActionButtons() {
        findViewById<MaterialButton>(R.id.buttonEditDevice).setOnClickListener { onEditClick() }
        findViewById<MaterialButton>(R.id.buttonDeleteDevice).setOnClickListener { onDeleteClick() }
    }

    private fun observeDevice() {
        deviceViewModel.observeAllDevices()
        deviceViewModel.devices.observe(this) { devices ->
            val device = devices.firstOrNull { it.id == deviceId }
            if (device == null) return@observe
            currentDevice = device
            updateHeaderInformation(device)
            updatePowerUI(device.status)
        }

        deviceViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateHeaderInformation(device: Device) {
        findViewById<ImageView>(R.id.imageDeviceStatusIcon).setImageResource(R.drawable.ic_iron)
        findViewById<TextView>(R.id.textDeviceStatusName).text = device.name
        findViewById<TextView>(R.id.textDeviceStatusLocation).text = "$floorName • Room"
        findViewById<TextView>(R.id.textConnectionStatus).text =
            if (device.status == DeviceStatus.DISCONNECTED) "Offline" else "Connected"

        findViewById<View>(R.id.viewConnectionStatusDot).backgroundTintList =
            ColorStateList.valueOf(
                Color.parseColor(if (device.status == DeviceStatus.DISCONNECTED) "#9E9E9E" else "#00BFA5")
            )
    }

    private fun setupPowerSwitch() {
        powerSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingFromFirebase) return@setOnCheckedChangeListener
            updateDeviceStatusInFirebase(isChecked)
        }
    }

    private fun updateDeviceStatusInFirebase(isOn: Boolean) {
        val device = currentDevice ?: return
        val newStatus = if (isOn) DeviceStatus.ON else DeviceStatus.OFF
        val updatedDevice = device.copy(status = newStatus)

        powerSwitch.isEnabled = false
        deviceViewModel.updateDevice(updatedDevice, onSuccess = {
            powerSwitch.isEnabled = true
            Toast.makeText(this, if (isOn) "Device ON" else "Device OFF", Toast.LENGTH_SHORT).show()
        }, onError = { msg ->
            powerSwitch.isEnabled = true
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        })
    }

    private fun updatePowerUI(status: DeviceStatus) {
        isUpdatingFromFirebase = true
        powerSwitch.isChecked = status == DeviceStatus.ON
        isUpdatingFromFirebase = false

        powerSwitch.text = if (status == DeviceStatus.ON) "ON" else "OFF"
        powerStateText.text = if (status == DeviceStatus.ON) "Device is ON" else "Device is OFF"
        powerStateText.setTextColor(Color.parseColor(if (status == DeviceStatus.ON) "#00BFA5" else "#716B76"))

        deviceStateText.text = if (status == DeviceStatus.ON) "ON" else "OFF"
        deviceStateText.setTextColor(Color.parseColor(if (status == DeviceStatus.ON) "#00BFA5" else "#9E9E9E"))
        deviceStateText.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor(if (status == DeviceStatus.ON) "#E0F2F1" else "#F5F5F5")
        )

        powerSwitch.isEnabled = status != DeviceStatus.ERROR && status != DeviceStatus.DISCONNECTED
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
            .setPositiveButton("Delete") { _, _ -> deleteDevice(device.id) }
            .show()
    }

    private fun deleteDevice(deviceId: String) {
        deviceViewModel.deleteDevice(deviceId, onSuccess = {
            Toast.makeText(this, "Device deleted", Toast.LENGTH_SHORT).show()
            finish()
        }, onError = { msg -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show() })
    }

    private fun setupSaveDurationButton() {
        findViewById<MaterialButton>(R.id.buttonSaveDuration).setOnClickListener {
            val duration = durationInput.text?.toString()?.toIntOrNull()
            if (duration == null || duration <= 0) {
                durationInput.error = "Enter a valid duration"
                durationInput.requestFocus()
                return@setOnClickListener
            }
            Toast.makeText(this, "Duration saved: $duration mins", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_DEVICE_ID = "device_id"
        const val EXTRA_FLOOR_NAME = "floor_name"
        const val EXTRA_GRID_ROWS = "grid_rows"
        const val EXTRA_GRID_COLUMNS = "grid_columns"
    }
}