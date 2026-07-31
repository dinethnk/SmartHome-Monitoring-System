package com.example.smarthome_monitoring_system.view.schedule

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class LightScheduleActivity : AppCompatActivity() {

    private lateinit var onTimeButton: MaterialButton
    private lateinit var offTimeButton: MaterialButton
    private lateinit var scheduleSwitch: MaterialSwitch

    private var onHour = 18
    private var onMinute = 0

    private var offHour = 6
    private var offMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_light_schedule)

        connectViews()
        setupTopBar()
        setupDeviceInformation()
        setupTimeButtons()
        setupSaveButton()
    }

    private fun connectViews() {
        onTimeButton =
            findViewById(R.id.buttonSelectOnTime)

        offTimeButton =
            findViewById(R.id.buttonSelectOffTime)

        scheduleSwitch =
            findViewById(R.id.switchScheduleEnabled)
    }

    private fun setupTopBar() {
        val backButton =
            findViewById<ImageButton>(R.id.buttonMenu)

        backButton.setImageResource(
            R.drawable.ic_arrow_left
        )

        backButton.contentDescription = "Go back"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupDeviceInformation() {
        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(R.drawable.ic_light)

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text = "Living Room Light"

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text = "Ground Floor • Living Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text = "Connected"

        findViewById<TextView>(
            R.id.textDeviceState
        ).text = "OFF"
    }

    private fun setupTimeButtons() {
        updateTimeButtonText()

        onTimeButton.setOnClickListener {
            showTimePicker(
                initialHour = onHour,
                initialMinute = onMinute
            ) { selectedHour, selectedMinute ->

                onHour = selectedHour
                onMinute = selectedMinute

                updateTimeButtonText()
            }
        }

        offTimeButton.setOnClickListener {
            showTimePicker(
                initialHour = offHour,
                initialMinute = offMinute
            ) { selectedHour, selectedMinute ->

                offHour = selectedHour
                offMinute = selectedMinute

                updateTimeButtonText()
            }
        }
    }

    private fun showTimePicker(
        initialHour: Int,
        initialMinute: Int,
        onTimeSelected: (Int, Int) -> Unit
    ) {
        val timePicker = TimePickerDialog(
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

    private fun updateTimeButtonText() {
        onTimeButton.text =
            formatTime(onHour, onMinute)

        offTimeButton.text =
            formatTime(offHour, offMinute)
    }

    private fun formatTime(
        hour: Int,
        minute: Int
    ): String {
        val period =
            if (hour < 12) "AM" else "PM"

        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        return String.format(
            "%02d:%02d %s",
            displayHour,
            minute,
            period
        )
    }

    private fun setupSaveButton() {
        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveSchedule
            )

        saveButton.setOnClickListener {
            saveSchedule()
        }
    }

    private fun saveSchedule() {
        if (scheduleSwitch.isChecked) {
            Toast.makeText(
                this,
                "Schedule saved: " +
                        "${formatTime(onHour, onMinute)} to " +
                        formatTime(offHour, offMinute),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this,
                "Automatic schedule disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        /*
         * Firebase schedule update will be added later.
         */
    }
}