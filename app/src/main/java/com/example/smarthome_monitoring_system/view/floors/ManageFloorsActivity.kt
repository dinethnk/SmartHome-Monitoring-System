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
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel
import android.content.Intent

class ManageFloorsActivity : AppCompatActivity() {

    private lateinit var floorViewModel: FloorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_manage_floors)

        floorViewModel =
            ViewModelProvider(this)[FloorViewModel::class.java]

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

        recyclerFloors.layoutManager =
            LinearLayoutManager(this)

        floorViewModel.floors.observe(this) { floors ->

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
                    }

                    startActivity(intent)
                }
        }

        floorViewModel.error.observe(this) { errorMessage ->

            if (!errorMessage.isNullOrEmpty()) {

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
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