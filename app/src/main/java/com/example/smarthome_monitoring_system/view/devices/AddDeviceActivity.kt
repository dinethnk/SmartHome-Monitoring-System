package com.example.smarthome_monitoring_system.view.devices

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.viewmodel.DeviceViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddDeviceActivity : AppCompatActivity() {

    private lateinit var deviceViewModel: DeviceViewModel

    private lateinit var editDeviceName: TextInputEditText
    private lateinit var editDeviceFloor: TextInputEditText
    private lateinit var editDeviceRow: TextInputEditText
    private lateinit var editDeviceColumn: TextInputEditText

    private lateinit var autoDeviceType: AutoCompleteTextView
    private lateinit var autoDeviceStatus: AutoCompleteTextView


    private var floorId: String = ""
    private var floorName: String = ""

    private var gridRows: Int = 8
    private var gridColumns: Int = 8


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_device)

        deviceViewModel =
            ViewModelProvider(this)[DeviceViewModel::class.java]

        readFloorInformation()
        connectViews()
        setupTopBar()
        setupDeviceTypeDropdown()
        setupDeviceStatusDropdown()
        setupSaveButton()
        setupCancelButton()
    }


    // ---------------------------------------------------------
    // Read floor information
    // ---------------------------------------------------------

    private fun readFloorInformation() {

        floorId =
            intent.getStringExtra(
                EXTRA_FLOOR_ID
            ).orEmpty()

        floorName =
            intent.getStringExtra(
                EXTRA_FLOOR_NAME
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
    }


    // ---------------------------------------------------------
    // Connect views
    // ---------------------------------------------------------

    private fun connectViews() {

        editDeviceName =
            findViewById(
                R.id.editDeviceName
            )

        editDeviceFloor =
            findViewById(
                R.id.editDeviceFloor
            )

        editDeviceRow =
            findViewById(
                R.id.editDeviceRow
            )

        editDeviceColumn =
            findViewById(
                R.id.editDeviceColumn
            )

        autoDeviceType =
            findViewById(
                R.id.autoDeviceType
            )

        autoDeviceStatus =
            findViewById(
                R.id.autoDeviceStatus
            )


        // Show current floor

        editDeviceFloor.setText(
            floorName
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
    // Device type dropdown
    // ---------------------------------------------------------

    private fun setupDeviceTypeDropdown() {

        val deviceTypes =
            DeviceType.values().map { type ->

                formatEnumName(
                    type.name
                )
            }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                deviceTypes
            )

        autoDeviceType.setAdapter(
            adapter
        )

        autoDeviceType.setOnClickListener {
            autoDeviceType.showDropDown()
        }
    }


    // ---------------------------------------------------------
    // Device status dropdown
    // ---------------------------------------------------------

    private fun setupDeviceStatusDropdown() {

        val statuses =
            DeviceStatus.values().map { status ->

                formatEnumName(
                    status.name
                )
            }

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                statuses
            )

        autoDeviceStatus.setAdapter(
            adapter
        )

        autoDeviceStatus.setText(
            formatEnumName(
                DeviceStatus.OFF.name
            ),
            false
        )

        autoDeviceStatus.setOnClickListener {
            autoDeviceStatus.showDropDown()
        }
    }


    // ---------------------------------------------------------
    // Save button
    // ---------------------------------------------------------

    private fun setupSaveButton() {

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveDevice
            )

        saveButton.setOnClickListener {

            validateAndSaveDevice(
                saveButton
            )
        }
    }


    // ---------------------------------------------------------
    // Cancel button
    // ---------------------------------------------------------

    private fun setupCancelButton() {

        val cancelButton =
            findViewById<MaterialButton>(
                R.id.buttonCancelDevice
            )

        cancelButton.setOnClickListener {
            finish()
        }
    }


    // ---------------------------------------------------------
    // Validate and save
    // ---------------------------------------------------------

    private fun validateAndSaveDevice(
        saveButton: MaterialButton
    ) {

        val deviceName =
            editDeviceName.text
                ?.toString()
                ?.trim()
                .orEmpty()


        val row =
            editDeviceRow.text
                ?.toString()
                ?.toIntOrNull()


        val column =
            editDeviceColumn.text
                ?.toString()
                ?.toIntOrNull()


        // -----------------------------------------------------
        // Validate device name
        // -----------------------------------------------------

        if (deviceName.isEmpty()) {

            editDeviceName.error =
                "Device name is required"

            editDeviceName.requestFocus()

            return
        }


        // -----------------------------------------------------
        // Validate floor
        // -----------------------------------------------------

        if (floorId.isBlank()) {

            Toast.makeText(
                this,
                "Floor information is missing",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        // -----------------------------------------------------
        // Validate device type
        // -----------------------------------------------------

        if (
            autoDeviceType.text
                .toString()
                .isBlank()
        ) {

            autoDeviceType.error =
                "Select a device type"

            return
        }


        // -----------------------------------------------------
        // Validate row
        // -----------------------------------------------------

        if (row == null) {

            editDeviceRow.error =
                "Enter a valid row"

            editDeviceRow.requestFocus()

            return
        }

        if (row < 0 || row >= gridRows) {

            editDeviceRow.error =
                "Row must be between 0 and ${gridRows - 1}"

            editDeviceRow.requestFocus()

            return
        }


        // -----------------------------------------------------
        // Validate column
        // -----------------------------------------------------

        if (column == null) {

            editDeviceColumn.error =
                "Enter a valid column"

            editDeviceColumn.requestFocus()

            return
        }

        if (column < 0 || column >= gridColumns) {

            editDeviceColumn.error =
                "Column must be between 0 and ${gridColumns - 1}"

            editDeviceColumn.requestFocus()

            return
        }


        // -----------------------------------------------------
        // Convert selected type
        // -----------------------------------------------------

        val deviceType =
            parseDeviceType(
                autoDeviceType.text
                    .toString()
            )


        // -----------------------------------------------------
        // Convert selected status
        // -----------------------------------------------------

        val deviceStatus =
            parseDeviceStatus(
                autoDeviceStatus.text
                    .toString()
            )


        // -----------------------------------------------------
        // Create device
        // -----------------------------------------------------

        val device =
            Device(

                name = deviceName,

                floorId = floorId,

                type = deviceType,

                status = deviceStatus,

                row = row,

                column = column
            )


        // -----------------------------------------------------
        // Disable button
        // -----------------------------------------------------

        saveButton.isEnabled = false
        saveButton.text = "Saving..."


        // -----------------------------------------------------
        // Save to Firebase
        // -----------------------------------------------------

        deviceViewModel.addDevice(

            device = device,

            onSuccess = {

                Toast.makeText(
                    this,
                    "$deviceName added successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            },

            onError = { errorMessage ->

                saveButton.isEnabled = true
                saveButton.text = "Save Device"

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // ---------------------------------------------------------
    // Convert enum to readable text
    // ---------------------------------------------------------

    private fun formatEnumName(
        value: String
    ): String {

        return value
            .lowercase()
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { word ->

                word.replaceFirstChar {
                    it.uppercase()
                }
            }
    }


    // ---------------------------------------------------------
    // Convert readable text to DeviceType
    // ---------------------------------------------------------

    private fun parseDeviceType(
        value: String
    ): DeviceType {

        return DeviceType.valueOf(
            value
                .uppercase()
                .replace(" ", "_")
        )
    }


    // ---------------------------------------------------------
    // Convert readable text to DeviceStatus
    // ---------------------------------------------------------

    private fun parseDeviceStatus(
        value: String
    ): DeviceStatus {

        return DeviceStatus.valueOf(
            value
                .uppercase()
                .replace(" ", "_")
        )
    }


    companion object {

        const val EXTRA_FLOOR_ID =
            "floor_id"

        const val EXTRA_FLOOR_NAME =
            "floor_name"

        const val EXTRA_GRID_ROWS =
            "grid_rows"

        const val EXTRA_GRID_COLUMNS =
            "grid_columns"
    }
}