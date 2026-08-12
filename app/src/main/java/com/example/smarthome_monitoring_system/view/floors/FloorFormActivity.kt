package com.example.smarthome_monitoring_system.view.floors

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil3.load
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

    private var isEditMode = false

    private var existingFloorId = ""

    private var existingCreatedAt = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_floor_form)

        floorViewModel =
            ViewModelProvider(this)[FloorViewModel::class.java]

        connectViews()
        readIntentData()
        setupTopBar()
        setupLivePreview()
        setupSaveButton()
        setupCancelButton()
    }


    // ---------------------------------------------------------
    // Connect views
    // ---------------------------------------------------------

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


    // ---------------------------------------------------------
    // Read Add/Edit information
    // ---------------------------------------------------------

    private fun readIntentData() {

        isEditMode =
            intent.getBooleanExtra(
                EXTRA_EDIT_MODE,
                false
            )

        if (isEditMode) {

            loadExistingFloor()

        } else {

            setupAddMode()
        }
    }


    // ---------------------------------------------------------
    // Add mode
    // ---------------------------------------------------------

    private fun setupAddMode() {

        findViewById<TextView>(
            R.id.textFloorFormTitle
        ).text = "Add New Floor"

        findViewById<MaterialButton>(
            R.id.buttonSaveFloor
        ).text = "Save Floor"

        updatePreview("")

        switchFloorActive.isChecked = true
    }


    // ---------------------------------------------------------
    // Edit mode
    // ---------------------------------------------------------

    private fun loadExistingFloor() {

        existingFloorId =
            intent.getStringExtra(
                EXTRA_FLOOR_ID
            ).orEmpty()

        existingCreatedAt =
            intent.getLongExtra(
                EXTRA_CREATED_AT,
                0L
            )

        val floorName =
            intent.getStringExtra(
                EXTRA_FLOOR_NAME
            ).orEmpty()

        val floorPlanUrl =
            intent.getStringExtra(
                EXTRA_FLOOR_PLAN_URL
            ).orEmpty()

        val gridRows =
            intent.getIntExtra(
                EXTRA_GRID_ROWS,
                8
            )

        val gridColumns =
            intent.getIntExtra(
                EXTRA_GRID_COLUMNS,
                8
            )


        // -----------------------------------------
        // Change screen title
        // -----------------------------------------

        findViewById<TextView>(
            R.id.textFloorFormTitle
        ).text = "Edit Floor"


        // -----------------------------------------
        // Change save button
        // -----------------------------------------

        findViewById<MaterialButton>(
            R.id.buttonSaveFloor
        ).text = "Update Floor"


        // -----------------------------------------
        // Fill existing values
        // -----------------------------------------

        editFloorName.setText(
            floorName
        )

        editFloorPlanUrl.setText(
            floorPlanUrl
        )

        editGridRows.setText(
            gridRows.toString()
        )

        editGridColumns.setText(
            gridColumns.toString()
        )


        // -----------------------------------------
        // Show existing floor-plan image
        // -----------------------------------------

        updatePreview(floorPlanUrl)


        /*
         * The current Floor model does not contain
         * an active field.
         *
         * Therefore this switch is currently only
         * a UI element and is not saved to Firebase.
         */
        switchFloorActive.isChecked = true
    }


    // ---------------------------------------------------------
    // Live preview
    // ---------------------------------------------------------

    private fun setupLivePreview() {

        editFloorPlanUrl.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}

                override fun afterTextChanged(
                    s: Editable?
                ) {
                    updatePreview(s?.toString().orEmpty())
                }
            }
        )
    }


    // ---------------------------------------------------------
    // Update preview image
    // ---------------------------------------------------------

    private fun updatePreview(url: String) {

        val isValidUrl =
            url.startsWith("http://") ||
            url.startsWith("https://")

        if (isValidUrl) {

            imageFloorPlanPreview.imageTintList = null

            imageFloorPlanPreview.load(url)

            imageFloorPlanPreview.scaleType =
                ImageView.ScaleType.CENTER_CROP

            floorPlanPlaceholder.visibility =
                View.GONE

        } else {

            imageFloorPlanPreview.imageTintList =
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this,
                        R.color.primary
                    )
                )

            imageFloorPlanPreview.setImageResource(
                R.drawable.ic_floor
            )

            imageFloorPlanPreview.scaleType =
                ImageView.ScaleType.CENTER_INSIDE

            floorPlanPlaceholder.visibility =
                View.VISIBLE
        }
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
    // Save / Update button
    // ---------------------------------------------------------

    private fun setupSaveButton() {

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveFloor
            )

        saveButton.setOnClickListener {

            validateAndSaveFloor()
        }
    }


    // ---------------------------------------------------------
    // Cancel button
    // ---------------------------------------------------------

    private fun setupCancelButton() {

        val cancelButton =
            findViewById<MaterialButton>(
                R.id.buttonCancelFloor
            )

        cancelButton.setOnClickListener {

            finish()
        }
    }


    // ---------------------------------------------------------
    // Validate form
    // ---------------------------------------------------------

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
        // Validate rows
        // -----------------------------------------

        if (gridRows == null || gridRows <= 0) {

            editGridRows.error =
                "Enter a valid row count"

            editGridRows.requestFocus()

            return
        }


        // -----------------------------------------
        // Validate columns
        // -----------------------------------------

        if (gridColumns == null || gridColumns <= 0) {

            editGridColumns.error =
                "Enter a valid column count"

            editGridColumns.requestFocus()

            return
        }


        // -----------------------------------------
        // Create timestamps
        // -----------------------------------------

        val currentTime =
            System.currentTimeMillis()

        val createdAt =
            if (
                isEditMode &&
                existingCreatedAt > 0L
            ) {
                existingCreatedAt
            } else {
                currentTime
            }


        // -----------------------------------------
        // Create Floor object
        // -----------------------------------------

        val floor = Floor(

            id = if (isEditMode) {
                existingFloorId
            } else {
                ""
            },

            name = floorName,

            floorPlanUrl = floorPlanUrl,

            gridRows = gridRows,

            gridColumns = gridColumns,

            createdAt = createdAt,

            updatedAt = currentTime
        )


        // -----------------------------------------
        // Disable button
        // -----------------------------------------

        val saveButton =
            findViewById<MaterialButton>(
                R.id.buttonSaveFloor
            )

        saveButton.isEnabled = false

        saveButton.text =
            if (isEditMode) {
                "Updating..."
            } else {
                "Saving..."
            }


        // -----------------------------------------
        // UPDATE existing floor
        // -----------------------------------------

        if (isEditMode) {

            floorViewModel.updateFloor(

                floor = floor,

                onSuccess = {

                    Toast.makeText(
                        this,
                        "$floorName updated successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                },

                onError = { errorMessage ->

                    saveButton.isEnabled = true

                    saveButton.text =
                        "Update Floor"

                    Toast.makeText(
                        this,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            )

            return
        }


        // -----------------------------------------
        // ADD new floor
        // -----------------------------------------

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

                saveButton.text =
                    "Save Floor"

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }


    // ---------------------------------------------------------
    // Intent constants
    // ---------------------------------------------------------

    companion object {

        const val EXTRA_EDIT_MODE =
            "edit_mode"

        const val EXTRA_FLOOR_ID =
            "floor_id"

        const val EXTRA_FLOOR_NAME =
            "floor_name"

        const val EXTRA_FLOOR_PLAN_URL =
            "floor_plan_url"

        const val EXTRA_GRID_ROWS =
            "grid_rows"

        const val EXTRA_GRID_COLUMNS =
            "grid_columns"

        const val EXTRA_CREATED_AT =
            "created_at"
    }
}