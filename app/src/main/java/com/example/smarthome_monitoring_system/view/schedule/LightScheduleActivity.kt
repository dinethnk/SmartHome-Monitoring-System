package com.example.smarthome_monitoring_system.view.schedule

import android.app.TimePickerDialog
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
import com.example.smarthome_monitoring_system.data.model.DeviceSchedule
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.view.devices.AddDeviceActivity
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.example.smarthome_monitoring_system.viewmodel.ScheduleViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class LightScheduleActivity : AppCompatActivity() {

    // =========================================================
    // ViewModels
    // =========================================================

    private lateinit var deviceViewModel: DeviceViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel


    // =========================================================
    // UI
    // =========================================================

    private lateinit var onTimeButton: MaterialButton
    private lateinit var offTimeButton: MaterialButton
    private lateinit var scheduleSwitch: MaterialSwitch

    private lateinit var lightPowerSwitch: MaterialSwitch
    private lateinit var powerStateText: TextView
    private lateinit var deviceStateChip: TextView


    // =========================================================
    // Device information
    // =========================================================

    private var deviceId: String = ""
    private var floorName: String = ""

    private var gridRows: Int = 8
    private var gridColumns: Int = 8

    private var currentDevice: Device? = null


    // =========================================================
    // Firebase/UI protection
    // =========================================================

    private var isUpdatingFromFirebase = false


    // =========================================================
    // Schedule time
    // =========================================================

    // Default ON time = 6:00 PM
    private var onHour = 18
    private var onMinute = 0

    // Default OFF time = 6:00 AM
    private var offHour = 6
    private var offMinute = 0


    // =========================================================
    // Activity lifecycle
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_light_schedule
        )


        // -----------------------------------------------------
        // ViewModels
        // -----------------------------------------------------

        deviceViewModel =
            ViewModelProvider(this)[DeviceViewModel::class.java]

        scheduleViewModel =
            ViewModelProvider(this)[ScheduleViewModel::class.java]


        // -----------------------------------------------------
        // Setup
        // -----------------------------------------------------

        readIntentInformation()

        TopBarHelper.setupNotifications(this)

        connectViews()

        setupTopBar()

        setupTimeButtons()

        setupSaveButton()

        setupPowerSwitch()

        setupActionButtons()


        // -----------------------------------------------------
        // Firebase observers
        // -----------------------------------------------------

        observeDevice()

        observeSchedule()
    }


    // =========================================================
    // Action buttons
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
    // Edit device
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
    // Delete device
    // =========================================================

    private fun onDeleteClick() {

        val device =
            currentDevice ?: return


        AlertDialog.Builder(this)

            .setTitle(
                "Delete Device?"
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

                deleteDevice(
                    device.id
                )
            }

            .show()
    }


    private fun deleteDevice(
        deviceId: String
    ) {

        deviceViewModel.deleteDevice(

            deviceId = deviceId,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Device deleted",
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
    // Read intent information
    // =========================================================

    private fun readIntentInformation() {

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
        }
    }


    // =========================================================
    // Connect views
    // =========================================================

    private fun connectViews() {

        onTimeButton =
            findViewById(
                R.id.buttonSelectOnTime
            )


        offTimeButton =
            findViewById(
                R.id.buttonSelectOffTime
            )


        scheduleSwitch =
            findViewById(
                R.id.switchScheduleEnabled
            )


        lightPowerSwitch =
            findViewById(
                R.id.switchLightPower
            )


        powerStateText =
            findViewById(
                R.id.textLightPowerState
            )


        deviceStateChip =
            findViewById(
                R.id.textDeviceState
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
    // Observe Firebase device
    // =========================================================

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


            currentDevice =
                device


            updateHeaderInformation(
                device
            )


            updatePowerUI(
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


    // =========================================================
    // Observe Firebase schedule
    // =========================================================

    private fun observeSchedule() {

        scheduleViewModel.observeSchedule(
            deviceId
        )


        scheduleViewModel.schedule.observe(
            this
        ) { schedule ->

            if (schedule == null) {
                return@observe
            }


            // ---------------------------------------------
            // Enable / disable switch
            // ---------------------------------------------

            scheduleSwitch.isChecked =
                schedule.enabled


            // ---------------------------------------------
            // Load ON time
            // ---------------------------------------------

            val onTimeParts =
                schedule.onTime.split(":")


            if (onTimeParts.size == 2) {

                onHour =
                    onTimeParts[0]
                        .toIntOrNull()
                        ?: onHour


                onMinute =
                    onTimeParts[1]
                        .toIntOrNull()
                        ?: onMinute
            }


            // ---------------------------------------------
            // Load OFF time
            // ---------------------------------------------

            val offTimeParts =
                schedule.offTime.split(":")


            if (offTimeParts.size == 2) {

                offHour =
                    offTimeParts[0]
                        .toIntOrNull()
                        ?: offHour


                offMinute =
                    offTimeParts[1]
                        .toIntOrNull()
                        ?: offMinute
            }


            // ---------------------------------------------
            // Update UI
            // ---------------------------------------------

            updateTimeButtonText()
        }


        scheduleViewModel.error.observe(
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
    // Header information
    // =========================================================

    private fun updateHeaderInformation(
        device: Device
    ) {

        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(
            R.drawable.ic_light
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
            if (
                device.status ==
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
                        device.status ==
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
    // Power switch
    // =========================================================

    private fun setupPowerSwitch() {

        lightPowerSwitch.setOnCheckedChangeListener {

                _,
                isChecked ->

            if (
                isUpdatingFromFirebase
            ) {

                return@setOnCheckedChangeListener
            }


            updateDeviceStatus(
                isChecked
            )
        }
    }


    // =========================================================
    // Update Firebase device status
    // =========================================================

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


        lightPowerSwitch.isEnabled =
            false


        deviceViewModel.updateDevice(

            device = updatedDevice,

            onSuccess = {

                lightPowerSwitch.isEnabled =
                    true


                Toast.makeText(
                    this,
                    if (isOn) {
                        "Light turned ON"
                    } else {
                        "Light turned OFF"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { errorMessage ->

                lightPowerSwitch.isEnabled =
                    true


                isUpdatingFromFirebase =
                    true


                lightPowerSwitch.isChecked =
                    device.status ==
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
    // Update power UI from Firebase
    // =========================================================

    private fun updatePowerUI(
        status: DeviceStatus
    ) {

        val isOn =
            status ==
                    DeviceStatus.ON


        isUpdatingFromFirebase =
            true


        lightPowerSwitch.isChecked =
            isOn


        isUpdatingFromFirebase =
            false


        when (status) {

            // -------------------------------------------------
            // ON
            // -------------------------------------------------

            DeviceStatus.ON -> {

                powerStateText.text =
                    "Light is ON"


                deviceStateChip.text =
                    "ON"


                deviceStateChip.setTextColor(
                    Color.parseColor(
                        "#00BFA5"
                    )
                )


                deviceStateChip.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#E0F2F1"
                        )
                    )


                lightPowerSwitch.isEnabled =
                    true
            }


            // -------------------------------------------------
            // OFF
            // -------------------------------------------------

            DeviceStatus.OFF -> {

                powerStateText.text =
                    "Light is OFF"


                deviceStateChip.text =
                    "OFF"


                deviceStateChip.setTextColor(
                    Color.parseColor(
                        "#9E9E9E"
                    )
                )


                deviceStateChip.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#F5F5F5"
                        )
                    )


                lightPowerSwitch.isEnabled =
                    true
            }


            // -------------------------------------------------
            // ERROR
            // -------------------------------------------------

            DeviceStatus.ERROR -> {

                powerStateText.text =
                    "Light has an error"


                deviceStateChip.text =
                    "ERROR"


                deviceStateChip.setTextColor(
                    Color.parseColor(
                        "#E53935"
                    )
                )


                deviceStateChip.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFEBEE"
                        )
                    )


                lightPowerSwitch.isEnabled =
                    false
            }


            // -------------------------------------------------
            // DISCONNECTED
            // -------------------------------------------------

            DeviceStatus.DISCONNECTED -> {

                powerStateText.text =
                    "Light is offline"


                deviceStateChip.text =
                    "OFFLINE"


                deviceStateChip.setTextColor(
                    Color.parseColor(
                        "#FFB300"
                    )
                )


                deviceStateChip.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFF8E1"
                        )
                    )


                lightPowerSwitch.isEnabled =
                    false
            }
        }
    }


    // =========================================================
    // Time buttons
    // =========================================================

    private fun setupTimeButtons() {

        updateTimeButtonText()


        // -----------------------------------------------------
        // ON time
        // -----------------------------------------------------

        onTimeButton.setOnClickListener {

            showTimePicker(

                initialHour =
                    onHour,

                initialMinute =
                    onMinute

            ) { selectedHour, selectedMinute ->

                onHour =
                    selectedHour

                onMinute =
                    selectedMinute


                updateTimeButtonText()
            }
        }


        // -----------------------------------------------------
        // OFF time
        // -----------------------------------------------------

        offTimeButton.setOnClickListener {

            showTimePicker(

                initialHour =
                    offHour,

                initialMinute =
                    offMinute

            ) { selectedHour, selectedMinute ->

                offHour =
                    selectedHour

                offMinute =
                    selectedMinute


                updateTimeButtonText()
            }
        }
    }


    // =========================================================
    // Time picker
    // =========================================================

    private fun showTimePicker(
        initialHour: Int,
        initialMinute: Int,
        onTimeSelected: (
            Int,
            Int
        ) -> Unit
    ) {

        val timePicker =
            TimePickerDialog(

                this,

                { _, selectedHour, selectedMinute ->

                    onTimeSelected(
                        selectedHour,
                        selectedMinute
                    )
                },

                initialHour,
                initialMinute,

                false
            )


        timePicker.show()
    }


    // =========================================================
    // Update displayed time
    // =========================================================

    private fun updateTimeButtonText() {

        onTimeButton.text =
            formatTime(
                onHour,
                onMinute
            )


        offTimeButton.text =
            formatTime(
                offHour,
                offMinute
            )
    }


    // =========================================================
    // Format time for UI
    // =========================================================

    private fun formatTime(
        hour: Int,
        minute: Int
    ): String {

        val period =
            if (hour < 12) {
                "AM"
            } else {
                "PM"
            }


        val displayHour =
            when {

                hour == 0 ->
                    12

                hour > 12 ->
                    hour - 12

                else ->
                    hour
            }


        return String.format(
            "%02d:%02d %s",
            displayHour,
            minute,
            period
        )
    }


    // =========================================================
    // Save schedule button
    // =========================================================

    private fun setupSaveButton() {

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveSchedule
            )


        saveButton.setOnClickListener {

            saveSchedule()
        }
    }


    // =========================================================
    // Save schedule to Firebase
    // =========================================================

    private fun saveSchedule() {

        if (deviceId.isBlank()) {

            Toast.makeText(
                this,
                "Device ID is missing",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        val schedule =
            DeviceSchedule(

                deviceId =
                    deviceId,

                enabled =
                    scheduleSwitch.isChecked,

                onTime =
                    String.format(
                        "%02d:%02d",
                        onHour,
                        onMinute
                    ),

                offTime =
                    String.format(
                        "%02d:%02d",
                        offHour,
                        offMinute
                    )
            )


        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveSchedule
            )


        saveButton.isEnabled =
            false

        saveButton.text =
            "Saving..."


        scheduleViewModel.saveSchedule(

            schedule =
                schedule,

            onSuccess = {

                saveButton.isEnabled =
                    true

                saveButton.text =
                    "Save Schedule"


                Toast.makeText(
                    this,
                    if (
                        schedule.enabled
                    ) {
                        "Schedule saved successfully"
                    } else {
                        "Automatic schedule disabled"
                    },
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { errorMessage ->

                saveButton.isEnabled =
                    true

                saveButton.text =
                    "Save Schedule"


                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // Intent constants
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