package com.example.smarthome_monitoring_system.view.floors

import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.viewmodel.FloorViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class FloorFormActivity : AppCompatActivity() {

    private lateinit var floorViewModel: FloorViewModel

    private lateinit var editFloorName: TextInputEditText
    private lateinit var editFloorPlanUrl: TextInputEditText
    private lateinit var editGridRows: TextInputEditText
    private lateinit var editGridColumns: TextInputEditText

    private lateinit var switchFloorActive: MaterialSwitch

    private lateinit var imageFloorPlanPreview: ImageView
    private lateinit var floorPlanPlaceholder: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_floor_form)

        floorViewModel =
            ViewModelProvider(this)[FloorViewModel::class.java]

        connectViews()
        setupTopBar()
        setupSaveButton()
        setupCancelButton()
    }

    private fun connectViews() {

        editFloorName =
            findViewById(R.id.editFloorName)

        editFloorPlanUrl =
            findViewById(R.id.editFloorPlanUrl)

        editGridRows =
            findViewById(R.id.editGridRows)

        editGridColumns =
            findViewById(R.id.editGridColumns)

        switchFloorActive =
            findViewById(R.id.switchFloorActive)

        imageFloorPlanPreview =
            findViewById(R.id.imageFloorPlanPreview)

        floorPlanPlaceholder =
            findViewById(R.id.textFloorPlanPlaceholder)
    }

    private fun setupTopBar() {

        val backButton =
            findViewById<ImageButton>(R.id.buttonMenu)

        backButton.setImageResource(
            R.drawable.ic_arrow_left
        )

        backButton.contentDescription =
            "Go back"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupSaveButton() {

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveFloor
            )

        saveButton.setOnClickListener {

            validateAndSaveFloor()
        }
    }

    private fun setupCancelButton() {

        val cancelButton =
            findViewById<MaterialButton>(
                R.id.buttonCancelFloor
            )

        cancelButton.setOnClickListener {

            finish()
        }
    }

    private fun validateAndSaveFloor() {

        val floorName =
            editFloorName.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val floorPlanUrl =
            editFloorPlanUrl.text
                ?.toString()
                ?.trim()
                .orEmpty()

        val gridRows =
            editGridRows.text
                ?.toString()
                ?.toIntOrNull()

        val gridColumns =
            editGridColumns.text
                ?.toString()
                ?.toIntOrNull()

        // -----------------------------------------
        // Validate floor name
        // -----------------------------------------

        if (floorName.isEmpty()) {

            editFloorName.error =
                "Floor name is required"

            editFloorName.requestFocus()

            return
        }

        // -----------------------------------------
        // Validate floor-plan URL
        // -----------------------------------------

        if (floorPlanUrl.isEmpty()) {

            editFloorPlanUrl.error =
                "Floor plan image URL is required"

            editFloorPlanUrl.requestFocus()

            return
        }

        if (
            !floorPlanUrl.startsWith("http://") &&
            !floorPlanUrl.startsWith("https://")
        ) {

            editFloorPlanUrl.error =
                "Enter a valid image URL"

            editFloorPlanUrl.requestFocus()

            return
        }

        // -----------------------------------------
        // Validate grid rows
        // -----------------------------------------

        if (gridRows == null || gridRows <= 0) {

            editGridRows.error =
                "Enter a valid row count"

            editGridRows.requestFocus()

            return
        }

        // -----------------------------------------
        // Validate grid columns
        // -----------------------------------------

        if (gridColumns == null || gridColumns <= 0) {

            editGridColumns.error =
                "Enter a valid column count"

            editGridColumns.requestFocus()

            return
        }

        // -----------------------------------------
        // Create Floor object
        // -----------------------------------------

        val currentTime =
            System.currentTimeMillis()

        val floor = Floor(

            name = floorName,

            floorPlanUrl = floorPlanUrl,

            gridRows = gridRows,

            gridColumns = gridColumns,

            createdAt = currentTime,

            updatedAt = currentTime
        )

        // -----------------------------------------
        // Save to Firebase
        // -----------------------------------------

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveFloor
            )

        saveButton.isEnabled = false

        saveButton.text = "Saving..."

        floorViewModel.addFloor(

            floor = floor,

            onSuccess = {

                Toast.makeText(
                    this,
                    "$floorName saved successfully",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            },

            onError = { errorMessage ->

                saveButton.isEnabled = true

                saveButton.text = "Save Floor"

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}