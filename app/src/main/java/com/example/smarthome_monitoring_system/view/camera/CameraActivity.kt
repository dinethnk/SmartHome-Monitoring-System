package com.example.smarthome_monitoring_system.view.camera

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    private lateinit var snapshotImage: ImageView
    private lateinit var snapshotTimeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)

        connectViews()
        setupTopBar()
        setupCameraInformation()
        setupRefreshButton()
    }

    private fun connectViews() {
        snapshotImage =
            findViewById(R.id.imageCameraSnapshot)

        snapshotTimeText =
            findViewById(R.id.textSnapshotTime)
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

    private fun setupCameraInformation() {
        findViewById<ImageView>(
            R.id.imageDeviceStatusIcon
        ).setImageResource(R.drawable.ic_camera)

        findViewById<TextView>(
            R.id.textDeviceStatusName
        ).text = "Living Room Camera"

        findViewById<TextView>(
            R.id.textDeviceStatusLocation
        ).text = "Ground Floor • Living Room"

        findViewById<TextView>(
            R.id.textConnectionStatus
        ).text = "Connected"

        findViewById<TextView>(
            R.id.textDeviceState
        ).text = "ON"
    }

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
        // Reload the local mock snapshot.
        snapshotImage.setImageResource(
            R.drawable.mock_camera_snapshot
        )

        snapshotImage.alpha = 0.4f

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
            timeFormatter.format(Date())

        snapshotTimeText.text =
            "Last refreshed: $currentTime"
    }
}