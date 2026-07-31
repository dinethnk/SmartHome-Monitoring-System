package com.example.smarthome_monitoring_system.view.devices

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class SafetyDeviceActivity : AppCompatActivity() {

    private lateinit var powerSwitch: MaterialSwitch
    private lateinit var powerStateText: TextView
    private lateinit var deviceStateText: TextView
    private lateinit var durationInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_safety_device)

        connectViews()
        setupTopBar()
        setupDeviceInformation()
        setupPowerSwitch()
        setupSaveDurationButton()
    }

    private fun connectViews() {
        powerSwitch =
            findViewById(R.id.switchSafetyPower)

        powerStateText =
            findViewById(R.id.textSafetyPowerState)

        deviceStateText =
            findViewById(R.id.textDeviceState)

        durationInput =
            findViewById(R.id.editMaximumDuration)
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
        ).setImageResource(R.drawable.ic_iron)

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text = "Clothing Iron"

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text = "Ground Floor • Utility Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text = "Connected"

        updatePowerUI(isOn = false)
    }

    private fun setupPowerSwitch() {
        powerSwitch.isChecked = false

        powerSwitch.setOnCheckedChangeListener {
                _,
                isChecked ->

            updatePowerUI(isChecked)

            /*
             * Firebase state update will be added here.
             */
        }
    }

    private fun updatePowerUI(isOn: Boolean) {
        powerSwitch.text =
            if (isOn) "ON" else "OFF"

        if (isOn) {
            powerStateText.text = "Device is ON"
            powerStateText.setTextColor(
                Color.parseColor("#00866A")
            )

            deviceStateText.text = "ON"
            deviceStateText.setTextColor(
                Color.parseColor("#00866A")
            )

            deviceStateText.backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#E4F6F0")
                )
        } else {
            powerStateText.text = "Device is OFF"
            powerStateText.setTextColor(
                Color.parseColor("#716B76")
            )

            deviceStateText.text = "OFF"
            deviceStateText.setTextColor(
                Color.parseColor("#716B76")
            )

            deviceStateText.backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#ECEAEC")
                )
        }
    }

    private fun setupSaveDurationButton() {
        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveDuration
            )

        saveButton.setOnClickListener {
            saveMaximumDuration()
        }
    }

    private fun saveMaximumDuration() {
        val duration =
            durationInput.text
                ?.toString()
                ?.toIntOrNull()

        if (duration == null || duration <= 0) {
            durationInput.error =
                "Enter a valid duration"

            durationInput.requestFocus()
            return
        }

        Toast.makeText(
            this,
            "Maximum duration saved: $duration minutes",
            Toast.LENGTH_SHORT
        ).show()

        /*
         * Firebase max_on_duration update
         * will be added here later.
         */
    }
}