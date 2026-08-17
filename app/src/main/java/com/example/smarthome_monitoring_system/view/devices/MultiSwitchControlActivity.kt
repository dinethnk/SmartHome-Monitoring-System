package com.example.smarthome_monitoring_system.view.devices

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.SwitchAdapter
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.SwitchChannel
import com.example.smarthome_monitoring_system.view.common.TopBarHelper
import com.example.smarthome_monitoring_system.viewmodel.MultiSwitchViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class MultiSwitchControlActivity :
    AppCompatActivity() {


    // =========================================================
    // VIEW MODEL
    // =========================================================

    private lateinit var viewModel:
            MultiSwitchViewModel


    // =========================================================
    // PARENT DEVICE
    // =========================================================

    private var deviceId: String = ""

    private var floorName: String = "Floor"

    private var gridRows: Int = 8

    private var gridColumns: Int = 8

    private var currentDevice: Device? = null


    // =========================================================
    // VIEWS
    // =========================================================

    private lateinit var masterSwitch:
            MaterialSwitch

    private lateinit var masterStateText:
            TextView

    private lateinit var recyclerSwitchChannels:
            RecyclerView

    private lateinit var switchAdapter:
            SwitchAdapter


    // =========================================================
    // SWITCH DATA
    // =========================================================

    private val switchChannels =
        mutableListOf<SwitchChannel>()


    // =========================================================
    // PREVENT FIREBASE LOOP
    // =========================================================

    private var isUpdatingFromFirebase =
        false


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_multi_switch_control
        )

        TopBarHelper.setupNotifications(
            this
        )

        viewModel =
            ViewModelProvider(this)[
                MultiSwitchViewModel::class.java
            ]

        readIntentData()

        connectViews()

        setupTopBar()

        setupMasterSwitch()

        setupActionButtons()

        setupAddSwitchButton()

        setupSwitchList()

        observeParentDevice()

        observeChildSwitches()
    }


    // =========================================================
    // READ INTENT DATA
    // =========================================================

    private fun readIntentData() {

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
                "Multi-switch ID is missing",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }


    // =========================================================
    // CONNECT VIEWS
    // =========================================================

    private fun connectViews() {

        masterSwitch =
            findViewById(
                R.id.switchMultiSwitchPower
            )


        masterStateText =
            findViewById(
                R.id.textMultiSwitchControlState
            )


        recyclerSwitchChannels =
            findViewById(
                R.id.recyclerSwitchChannels
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
    // MASTER SWITCH
    // =========================================================

    private fun setupMasterSwitch() {

        masterSwitch
            .setOnCheckedChangeListener {

                    _,
                    isChecked ->

                if (
                    isUpdatingFromFirebase
                ) {
                    return@setOnCheckedChangeListener
                }

                updateParentStatus(
                    isChecked
                )
            }
    }


    // =========================================================
    // PARENT EDIT / DELETE
    // =========================================================

    private fun setupActionButtons() {

        findViewById<MaterialButton>(
            R.id.buttonEditMultiSwitch
        ).setOnClickListener {

            editDevice()
        }


        findViewById<MaterialButton>(
            R.id.buttonDeleteMultiSwitch
        ).setOnClickListener {

            confirmDelete()
        }
    }


    // =========================================================
    // ADD CHILD SWITCH
    // =========================================================

    private fun setupAddSwitchButton() {

        findViewById<MaterialButton>(
            R.id.buttonAddSwitch
        ).setOnClickListener {

            showAddSwitchDialog()
        }
    }


    // =========================================================
    // CHILD SWITCH LIST
    // =========================================================

    private fun setupSwitchList() {

        recyclerSwitchChannels.layoutManager =
            LinearLayoutManager(this)


        switchAdapter =
            SwitchAdapter(

                switchChannels,

                // -------------------------------------------------
                // ON / OFF
                // -------------------------------------------------

                onSwitchChanged = {

                        switchChannel ->

                    updateChildSwitchStatus(
                        switchChannel
                    )
                },

                // -------------------------------------------------
                // EDIT
                // -------------------------------------------------

                onEditClicked = {

                        switchChannel ->

                    showEditSwitchDialog(
                        switchChannel
                    )
                },

                // -------------------------------------------------
                // DELETE
                // -------------------------------------------------

                onDeleteClicked = {

                        switchChannel ->

                    confirmDeleteSwitch(
                        switchChannel
                    )
                }
            )


        recyclerSwitchChannels.adapter =
            switchAdapter
    }


    // =========================================================
    // OBSERVE PARENT DEVICE
    // =========================================================

    private fun observeParentDevice() {

        viewModel.observeDevice(
            deviceId
        )


        viewModel.device.observe(
            this
        ) { device ->

            if (device == null) {
                return@observe
            }


            currentDevice =
                device


            updateDeviceInformation(
                device
            )


            updateParentUI(
                device.status
            )

            // Update individual switches enabled/disabled state
            if (::switchAdapter.isInitialized) {
                switchAdapter.updateMasterState(device.status == DeviceStatus.ON)
            }
        }


        viewModel.error.observe(
            this
        ) { message ->

            if (
                !message.isNullOrEmpty()
            ) {

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // =========================================================
    // OBSERVE CHILD SWITCHES
    // =========================================================

    private fun observeChildSwitches() {

        viewModel.observeSwitchChannels(
            deviceId
        )


        viewModel.switchChannels.observe(
            this
        ) { channels ->

            switchChannels.clear()

            switchChannels.addAll(
                channels
            )

            // Ensure master state is correct for new data
            currentDevice?.let { device ->
                switchAdapter.updateMasterState(device.status == DeviceStatus.ON)
            }

            switchAdapter.notifyDataSetChanged()
        }
    }


    // =========================================================
    // DEVICE INFORMATION
    // =========================================================

    private fun updateDeviceInformation(
        device: Device
    ) {

        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(
            R.drawable.ic_multi_switch
        )


        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text =
            device.name


        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text =
            "$floorName • Living Room"


        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text =
            if (
                device.status ==
                DeviceStatus.DISCONNECTED
            ) {

                "Disconnected"

            } else {

                "Connected"
            }
    }


    // =========================================================
    // UPDATE PARENT UI
    // =========================================================

    private fun updateParentUI(
        status: DeviceStatus
    ) {

        val isOn =
            status == DeviceStatus.ON


        isUpdatingFromFirebase =
            true


        masterSwitch.isChecked =
            isOn


        isUpdatingFromFirebase =
            false


        when (status) {

            DeviceStatus.ON -> {

                masterStateText.text =
                    "Switch unit is ON"

                masterStateText.setTextColor(
                    Color.parseColor(
                        "#00866A"
                    )
                )

                masterSwitch.isEnabled =
                    true
            }


            DeviceStatus.OFF -> {

                masterStateText.text =
                    "Switch unit is OFF"

                masterStateText.setTextColor(
                    Color.parseColor(
                        "#716B76"
                    )
                )

                masterSwitch.isEnabled =
                    true
            }


            DeviceStatus.ERROR -> {

                masterStateText.text =
                    "Switch unit has an error"

                masterStateText.setTextColor(
                    Color.parseColor(
                        "#E53935"
                    )
                )

                masterSwitch.isEnabled =
                    false
            }


            DeviceStatus.DISCONNECTED -> {

                masterStateText.text =
                    "Switch unit is disconnected"

                masterStateText.setTextColor(
                    Color.parseColor(
                        "#FFB300"
                    )
                )

                masterSwitch.isEnabled =
                    false
            }
        }


        updateHeaderStatusBadge(
            status
        )
    }


    // =========================================================
    // HEADER STATUS BADGE
    // =========================================================

    private fun updateHeaderStatusBadge(
        status: DeviceStatus
    ) {

        val deviceState =
            findViewById<TextView>(
                R.id.textDeviceState
            )


        when (status) {

            DeviceStatus.ON -> {

                deviceState.text =
                    "ON"

                deviceState.setTextColor(
                    Color.parseColor(
                        "#00866A"
                    )
                )

                deviceState.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#E4F6F0"
                        )
                    )
            }


            DeviceStatus.OFF -> {

                deviceState.text =
                    "OFF"

                deviceState.setTextColor(
                    Color.parseColor(
                        "#716B76"
                    )
                )

                deviceState.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#ECEAEC"
                        )
                    )
            }


            DeviceStatus.ERROR -> {

                deviceState.text =
                    "ERROR"

                deviceState.setTextColor(
                    Color.parseColor(
                        "#E53935"
                    )
                )

                deviceState.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFEBEE"
                        )
                    )
            }


            DeviceStatus.DISCONNECTED -> {

                deviceState.text =
                    "OFFLINE"

                deviceState.setTextColor(
                    Color.parseColor(
                        "#FFB300"
                    )
                )

                deviceState.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFF8E1"
                        )
                    )
            }
        }
    }


    // =========================================================
    // UPDATE PARENT STATUS
    // =========================================================

    private fun updateParentStatus(
        isOn: Boolean
    ) {

        val device =
            currentDevice
                ?: return


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


        masterSwitch.isEnabled =
            false


        viewModel.updateDevice(

            device = updatedDevice,

            onSuccess = {

                masterSwitch.isEnabled =
                    true

                // -------------------------------------------------
                // If master is OFF, turn off all child switches
                // in the database as well (Extension Cable logic).
                // -------------------------------------------------
                if (!isOn) {
                    viewModel.turnOffAllChildSwitches(deviceId)
                }
            },

            onError = { message ->

                masterSwitch.isEnabled =
                    true


                isUpdatingFromFirebase =
                    true


                masterSwitch.isChecked =
                    device.status ==
                            DeviceStatus.ON


                isUpdatingFromFirebase =
                    false


                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // ADD CHILD SWITCH DIALOG
    // =========================================================

    private fun showAddSwitchDialog() {

        val input =
            EditText(this)

        input.hint =
            "Switch name"

        input.setSingleLine(
            true
        )


        val container =
            FrameLayout(this)

        container.setPadding(
            50,
            0,
            50,
            0
        )


        container.addView(
            input
        )


        AlertDialog.Builder(this)

            .setTitle(
                "Add Switch"
            )

            .setMessage(
                "Enter a name for the new switch."
            )

            .setView(
                container
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Add"
            ) { _, _ ->

                val name =
                    input.text
                        .toString()
                        .trim()


                if (
                    name.isBlank()
                ) {

                    Toast.makeText(
                        this,
                        "Switch name is required",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }


                addChildSwitch(
                    name
                )
            }

            .show()
    }


    // =========================================================
    // ADD CHILD SWITCH
    // =========================================================

    private fun addChildSwitch(
        name: String
    ) {

        val newSwitch =
            SwitchChannel(

                id = "",

                name = name,

                isOn = false
            )


        viewModel.addSwitchChannel(

            deviceId = deviceId,

            switchChannel = newSwitch,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Switch added",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // UPDATE CHILD SWITCH
    // =========================================================

    private fun updateChildSwitchStatus(
        switchChannel: SwitchChannel
    ) {

        viewModel.updateSwitchChannel(

            deviceId = deviceId,

            switchChannel = switchChannel,

            onSuccess = {
                // Log event for individual switch toggle
                val eventMessage = "${switchChannel.name} was turned ${if (switchChannel.isOn) "ON" else "OFF"}"
                
                com.example.smarthome_monitoring_system.data.firebase.FirebaseDataSource.eventsReference
                    .push()
                    .setValue(
                        com.example.smarthome_monitoring_system.data.model.HomeEvent(
                            deviceId = "${deviceId}_${switchChannel.id}",
                            deviceName = currentDevice?.name ?: "Switch",
                            type = if (switchChannel.isOn) "POWER_ON" else "POWER_OFF",
                            message = eventMessage,
                            timestamp = System.currentTimeMillis()
                        )
                    )
            },

            onError = { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // EDIT CHILD SWITCH
    // =========================================================

    private fun showEditSwitchDialog(
        switchChannel: SwitchChannel
    ) {

        val input =
            EditText(this)

        input.setSingleLine(
            true
        )

        input.setText(
            switchChannel.name
        )


        val container =
            FrameLayout(this)

        container.setPadding(
            50,
            0,
            50,
            0
        )


        container.addView(
            input
        )


        AlertDialog.Builder(this)

            .setTitle(
                "Edit Switch"
            )

            .setMessage(
                "Change the switch name."
            )

            .setView(
                container
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Save"
            ) { _, _ ->

                val newName =
                    input.text
                        .toString()
                        .trim()


                if (
                    newName.isBlank()
                ) {

                    Toast.makeText(
                        this,
                        "Switch name is required",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }


                val updatedSwitch =
                    switchChannel.copy(
                        name = newName
                    )


                viewModel.updateSwitchChannel(

                    deviceId = deviceId,

                    switchChannel =
                        updatedSwitch,

                    onSuccess = {

                        Toast.makeText(
                            this,
                            "Switch updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    },

                    onError = { message ->

                        Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }

            .show()
    }


    // =========================================================
    // DELETE CHILD SWITCH CONFIRMATION
    // =========================================================

    private fun confirmDeleteSwitch(
        switchChannel: SwitchChannel
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Delete Switch?"
            )

            .setMessage(
                "Are you sure you want to delete \"${switchChannel.name}\"?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteChildSwitch(
                    switchChannel
                )
            }

            .show()
    }


    // =========================================================
    // DELETE CHILD SWITCH
    // =========================================================

    private fun deleteChildSwitch(
        switchChannel: SwitchChannel
    ) {

        if (
            switchChannel.id.isBlank()
        ) {

            Toast.makeText(
                this,
                "Switch ID is missing",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        viewModel.deleteSwitchChannel(

            deviceId = deviceId,

            switchId = switchChannel.id,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Switch deleted",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // EDIT PARENT DEVICE
    // =========================================================

    private fun editDevice() {

        val device =
            currentDevice
                ?: return


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


        startActivity(
            intent
        )
    }


    // =========================================================
    // DELETE PARENT CONFIRMATION
    // =========================================================

    private fun confirmDelete() {

        val device =
            currentDevice
                ?: return


        AlertDialog.Builder(this)

            .setTitle(
                "Delete Switch Unit?"
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


    // =========================================================
    // DELETE PARENT DEVICE
    // =========================================================

    private fun deleteDevice(
        deviceId: String
    ) {

        viewModel.deleteDevice(

            deviceId = deviceId,

            onSuccess = {

                Toast.makeText(
                    this,
                    "Switch unit deleted",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            },

            onError = { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // =========================================================
    // INTENT EXTRAS
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