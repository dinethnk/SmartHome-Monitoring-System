package com.example.smarthome_monitoring_system.view.floors

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome_monitoring_system.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText

class FloorFormActivity : AppCompatActivity() {

    private var selectedFloorPlanUri: Uri? = null

    private lateinit var editFloorName: TextInputEditText
    private lateinit var editGridRows: TextInputEditText
    private lateinit var editGridColumns: TextInputEditText
    private lateinit var switchFloorActive: MaterialSwitch
    private lateinit var imageFloorPlanPreview: ImageView
    private lateinit var floorPlanPlaceholder: TextView

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { selectedUri ->

            if (selectedUri != null) {
                selectedFloorPlanUri = selectedUri

                imageFloorPlanPreview.imageTintList = null
                imageFloorPlanPreview.setImageURI(selectedUri)
                imageFloorPlanPreview.scaleType =
                    ImageView.ScaleType.CENTER_CROP

                floorPlanPlaceholder.visibility = View.GONE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_floor_form)

        connectViews()
        setupTopBar()
        setupImageSelection()
        setupSaveButton()
        setupCancelButton()
    }

    private fun connectViews() {
        editFloorName =
            findViewById(R.id.editFloorName)

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

        backButton.contentDescription = "Go back"

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupImageSelection() {
        val selectImageButton =
            findViewById<MaterialButton>(
                R.id.buttonSelectFloorPlan
            )

        selectImageButton.setOnClickListener {
            imagePicker.launch("image/*")
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
            editFloorName.text?.toString()?.trim().orEmpty()

        val gridRows =
            editGridRows.text?.toString()?.toIntOrNull()

        val gridColumns =
            editGridColumns.text?.toString()?.toIntOrNull()

        if (floorName.isEmpty()) {
            editFloorName.error = "Floor name is required"
            editFloorName.requestFocus()
            return
        }

        if (gridRows == null || gridRows <= 0) {
            editGridRows.error = "Enter a valid row count"
            editGridRows.requestFocus()
            return
        }

        if (gridColumns == null || gridColumns <= 0) {
            editGridColumns.error =
                "Enter a valid column count"

            editGridColumns.requestFocus()
            return
        }

        if (selectedFloorPlanUri == null) {
            Toast.makeText(
                this,
                "Please choose a floor-plan image",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val activeStatus =
            if (switchFloorActive.isChecked) {
                "Active"
            } else {
                "Inactive"
            }

        Toast.makeText(
            this,
            "$floorName saved as $activeStatus",
            Toast.LENGTH_LONG
        ).show()

        /*
         * Firebase upload and database saving will be
         * added here later.
         */

        finish()
    }
}