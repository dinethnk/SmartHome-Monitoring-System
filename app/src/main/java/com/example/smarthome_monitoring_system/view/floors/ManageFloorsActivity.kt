package com.example.smarthome_monitoring_system.view.floors

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.FloorAdapter
import com.example.smarthome_monitoring_system.data.model.Floor
import android.content.Intent

class ManageFloorsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_floors)

        setupTopBar()
        setupFloorList()
        setupAddFloorButton()
    }

    private fun setupTopBar() {
        val backButton =
            findViewById<ImageButton>(R.id.buttonMenu)

        backButton.setImageResource(R.drawable.ic_arrow_left)
        backButton.contentDescription = "Go back"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupFloorList() {
        val recyclerFloors =
            findViewById<RecyclerView>(R.id.recyclerFloors)

        val floors = listOf(
            Floor(
                id = "floor_001",
                name = "Ground Floor",
                floorPlanUrl = "",
                gridRows = 8,
                gridColumns = 8,
                deviceCount = 3,
                active = true
            ),
            Floor(
                id = "floor_002",
                name = "First Floor",
                floorPlanUrl = "",
                gridRows = 8,
                gridColumns = 8,
                deviceCount = 4,
                active = true
            ),
            Floor(
                id = "floor_003",
                name = "Second Floor",
                floorPlanUrl = "",
                gridRows = 6,
                gridColumns = 6,
                deviceCount = 2,
                active = false
            )
        )

        recyclerFloors.layoutManager =
            LinearLayoutManager(this)

        recyclerFloors.adapter =
            FloorAdapter(floors) { selectedFloor ->

                val intent = Intent(
                    this,
                    FloorPlanActivity::class.java
                ).apply {
                    putExtra(
                        FloorPlanActivity.EXTRA_FLOOR_NAME,
                        selectedFloor.name
                    )

                    putExtra(
                        FloorPlanActivity.EXTRA_GRID_ROWS,
                        selectedFloor.gridRows
                    )

                    putExtra(
                        FloorPlanActivity.EXTRA_GRID_COLUMNS,
                        selectedFloor.gridColumns
                    )

                    putExtra(
                        FloorPlanActivity.EXTRA_DEVICE_COUNT,
                        selectedFloor.deviceCount
                    )
                }

                startActivity(intent)
            }
    }

    private fun setupAddFloorButton() {
        val addFloorButton =
            findViewById<MaterialButton>(
                R.id.buttonAddFloor
            )

        addFloorButton.setOnClickListener {
            val intent = Intent(
                this,
                FloorFormActivity::class.java
            )

            startActivity(intent)
        }
    }
}