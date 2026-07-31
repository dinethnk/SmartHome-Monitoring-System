package com.example.smarthome_monitoring_system.view.devices

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.SwitchAdapter
import com.example.smarthome_monitoring_system.data.model.SwitchChannel

class MultiSwitchControlActivity : AppCompatActivity() {

    private val switchChannels = mutableListOf(
        SwitchChannel(
            id = "switch_1",
            name = "Main Light",
            isOn = false
        ),
        SwitchChannel(
            id = "switch_2",
            name = "Wall Light",
            isOn = false
        ),
        SwitchChannel(
            id = "switch_3",
            name = "Ceiling Light",
            isOn = false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_multi_switch_control
        )

        setupTopBar()
        setupDeviceInformation()
        setupSwitchList()
        updateUnitStatus()
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
        ).text = "Living Room Switch Unit"

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text = "Ground Floor • Living Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text = "Connected"
    }

    private fun setupSwitchList() {
        val recyclerSwitchChannels =
            findViewById<RecyclerView>(
                R.id.recyclerSwitchChannels
            )

        recyclerSwitchChannels.layoutManager =
            LinearLayoutManager(this)

        recyclerSwitchChannels.adapter =
            SwitchAdapter(switchChannels) {
                    _,
                    _ ->

                updateUnitStatus()

                /*
                 * Firebase channel update will be added here.
                 */
            }
    }

    private fun updateUnitStatus() {
        val deviceState =
            findViewById<TextView>(
                R.id.textDeviceState
            )

        val anySwitchOn =
            switchChannels.any { it.isOn }

        if (anySwitchOn) {
            deviceState.text = "ON"

            deviceState.setTextColor(
                Color.parseColor("#00866A")
            )

            deviceState.backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#E4F6F0")
                )
        } else {
            deviceState.text = "OFF"

            deviceState.setTextColor(
                Color.parseColor("#716B76")
            )

            deviceState.backgroundTintList =
                ColorStateList.valueOf(
                    Color.parseColor("#ECEAEC")
                )
        }
    }
}