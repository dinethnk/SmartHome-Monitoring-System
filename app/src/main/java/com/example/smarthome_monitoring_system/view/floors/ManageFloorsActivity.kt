package com.example.smarthome_monitoring_system.view.floors

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.FloorAdapter
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel
import com.google.android.material.button.MaterialButton

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
    // Floor list
    // ---------------------------------------------------------

    private fun setupFloorList() {

        val recyclerFloors =
            findViewById<RecyclerView>(
                R.id.recyclerFloors
            )

        recyclerFloors.layoutManager =
            LinearLayoutManager(this)

        floorViewModel.floors.observe(this) { floors ->

            recyclerFloors.adapter =
                FloorAdapter(

                    floors = floors,

                    // -----------------------------------------
                    // Open floor
                    // -----------------------------------------

                    onFloorClick = { selectedFloor ->

                        openFloorPlan(
                            selectedFloor
                        )
                    },

                    // -----------------------------------------
                    // Edit floor
                    // -----------------------------------------

                    onEditFloor = { selectedFloor ->

                        editFloor(
                            selectedFloor
                        )
                    },

                    // -----------------------------------------
                    // Delete floor
                    // -----------------------------------------

                    onDeleteFloor = { selectedFloor ->

                        confirmDeleteFloor(
                            selectedFloor
                        )
                    }
                )
        }


        // -----------------------------------------------------
        // Firebase error
        // -----------------------------------------------------

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


    // ---------------------------------------------------------
    // Open floor plan
    // ---------------------------------------------------------

    private fun openFloorPlan(
        floor: Floor
    ) {

        val intent =
            Intent(
                this,
                FloorPlanActivity::class.java
            ).apply {

                // Firebase floor ID
                putExtra(
                    FloorPlanActivity.EXTRA_FLOOR_ID,
                    floor.id
                )

                // Floor name
                putExtra(
                    FloorPlanActivity.EXTRA_FLOOR_NAME,
                    floor.name
                )

                // Grid configuration
                putExtra(
                    FloorPlanActivity.EXTRA_GRID_ROWS,
                    floor.gridRows
                )

                putExtra(
                    FloorPlanActivity.EXTRA_GRID_COLUMNS,
                    floor.gridColumns
                )

                // Floor-plan image
                putExtra(
                    FloorPlanActivity.EXTRA_FLOOR_PLAN_URL,
                    floor.floorPlanUrl
                )
            }

        startActivity(intent)
    }


    // ---------------------------------------------------------
    // Edit floor
    // ---------------------------------------------------------

    private fun editFloor(
        floor: Floor
    ) {

        val intent =
            Intent(
                this,
                FloorFormActivity::class.java
            ).apply {

                putExtra(
                    FloorFormActivity.EXTRA_EDIT_MODE,
                    true
                )

                putExtra(
                    FloorFormActivity.EXTRA_FLOOR_ID,
                    floor.id
                )

                putExtra(
                    FloorFormActivity.EXTRA_FLOOR_NAME,
                    floor.name
                )

                putExtra(
                    FloorFormActivity.EXTRA_FLOOR_PLAN_URL,
                    floor.floorPlanUrl
                )

                putExtra(
                    FloorFormActivity.EXTRA_GRID_ROWS,
                    floor.gridRows
                )

                putExtra(
                    FloorFormActivity.EXTRA_GRID_COLUMNS,
                    floor.gridColumns
                )
            }

        startActivity(intent)
    }


    // ---------------------------------------------------------
    // Delete confirmation
    // ---------------------------------------------------------

    private fun confirmDeleteFloor(
        floor: Floor
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Floor?")
            .setMessage(
                "Are you sure you want to delete \"${floor.name}\"?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteFloor(
                    floor
                )
            }
            .show()
    }


    // ---------------------------------------------------------
    // Delete floor
    // ---------------------------------------------------------

    private fun deleteFloor(
        floor: Floor
    ) {

        floorViewModel.deleteFloor(

            floorId = floor.id,

            onSuccess = {

                Toast.makeText(
                    this,
                    "${floor.name} deleted successfully",
                    Toast.LENGTH_SHORT
                ).show()
            },

            onError = { errorMessage ->

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // ---------------------------------------------------------
    // Add floor
    // ---------------------------------------------------------

    private fun setupAddFloorButton() {

        val addFloorButton =
            findViewById<MaterialButton>(
                R.id.buttonAddFloor
            )

        addFloorButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    FloorFormActivity::class.java
                )

            startActivity(intent)
        }
    }
}