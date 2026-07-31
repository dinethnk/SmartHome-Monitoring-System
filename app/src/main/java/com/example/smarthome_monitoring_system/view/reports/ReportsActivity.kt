package com.example.smarthome_monitoring_system.view.reports

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.UsageRecordAdapter
import com.example.smarthome_monitoring_system.data.model.UsageRecord

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reports)

        setupTopBar()
        setupUsageRecords()
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

    private fun setupUsageRecords() {
        val recyclerUsageRecords =
            findViewById<RecyclerView>(
                R.id.recyclerUsageRecords
            )

        val currentTime =
            System.currentTimeMillis()

        val usageRecords = listOf(
            UsageRecord(
                id = "usage_001",
                deviceId = "outlet_001",
                deviceName = "Living Room Outlet",
                durationMinutes = 45,
                timestamp = currentTime
            ),
            UsageRecord(
                id = "usage_002",
                deviceId = "iron_001",
                deviceName = "Clothing Iron",
                durationMinutes = 20,
                timestamp = currentTime - 3_600_000L
            ),
            UsageRecord(
                id = "usage_003",
                deviceId = "light_001",
                deviceName = "Living Room Light",
                durationMinutes = 180,
                timestamp = currentTime - 7_200_000L
            ),
            UsageRecord(
                id = "usage_004",
                deviceId = "outlet_002",
                deviceName = "Bedroom Outlet",
                durationMinutes = 60,
                timestamp = currentTime - 86_400_000L
            )
        )

        recyclerUsageRecords.layoutManager =
            LinearLayoutManager(this)

        recyclerUsageRecords.adapter =
            UsageRecordAdapter(usageRecords)
    }
}