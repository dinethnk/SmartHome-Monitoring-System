package com.example.smarthome_monitoring_system.view.devices

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R
import com.google.android.material.materialswitch.MaterialSwitch

class OutletControlActivity : AppCompatActivity() {

    private lateinit var outletSwitch: MaterialSwitch
    private lateinit var controlStateText: TextView
    private lateinit var deviceStateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_outlet_control)

        connectViews()
        setupTopBar()
        setupDeviceInformation()
        setupOutletSwitch()
    }

    private fun connectViews() {
        outletSwitch =
            findViewById(R.id.switchOutletPower)

        controlStateText =
            findViewById(R.id.textOutletControlState)

        deviceStateText =
            findViewById(R.id.textDeviceState)
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
        ).setImageResource(R.drawable.ic_power)

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text = "Living Room Outlet"

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text = "Ground Floor • Living Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text = "Connected"

        updateOutletUI(isOn = false)
    }

    private fun setupOutletSwitch() {
        outletSwitch.isChecked = false

        outletSwitch.setOnCheckedChangeListener {
                _,
                isChecked ->

            updateOutletUI(isChecked)

            /*
             * Firebase state update will be added here.
             */
        }
    }

    private fun updateOutletUI(isOn: Boolean) {
        outletSwitch.text = if (isOn) "ON" else "OFF"

        if (isOn) {
            controlStateText.text = "Outlet is ON"
            controlStateText.setTextColor(
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
            controlStateText.text = "Outlet is OFF"
            controlStateText.setTextColor(
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
}