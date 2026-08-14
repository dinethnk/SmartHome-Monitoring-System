package com.example.smarthome_monitoring_system.view.alerts

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.adapter.AlertAdapter
import com.example.smarthome_monitoring_system.viewmodel.AlertViewModel

class AlertsActivity : AppCompatActivity() {

    private lateinit var alertViewModel: AlertViewModel

    private lateinit var alertAdapter: AlertAdapter


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_alerts
        )


        // =====================================================
        // VIEW MODEL
        // =====================================================

        alertViewModel =
            ViewModelProvider(this)[
                AlertViewModel::class.java
            ]


        // =====================================================
        // SETUP UI
        // =====================================================

        setupTopBar()

        setupAlertsRecyclerView()

        observeAlerts()
    }


    // =========================================================
    // TOP BAR
    // =========================================================

    private fun setupTopBar() {

        // -----------------------------------------------------
        // Back button
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // Hide notification button
        // -----------------------------------------------------
        // We are already on the Alerts screen, so showing
        // another notification button is unnecessary.
        // -----------------------------------------------------

        findViewById<View>(
            R.id.containerNotifications
        ).visibility =
            View.GONE
    }


    // =========================================================
    // ALERT RECYCLER VIEW
    // =========================================================

    private fun setupAlertsRecyclerView() {

        val recyclerAlerts =
            findViewById<RecyclerView>(
                R.id.recyclerAlerts
            )


        // -----------------------------------------------------
        // Create adapter
        // -----------------------------------------------------

        alertAdapter =
            AlertAdapter(
                emptyList()
            )


        // -----------------------------------------------------
        // RecyclerView configuration
        // -----------------------------------------------------

        recyclerAlerts.layoutManager =
            LinearLayoutManager(this)

        recyclerAlerts.adapter =
            alertAdapter

        recyclerAlerts.isNestedScrollingEnabled =
            false

        recyclerAlerts.overScrollMode =
            View.OVER_SCROLL_NEVER
    }


    // =========================================================
    // OBSERVE FIREBASE ALERTS
    // =========================================================

    private fun observeAlerts() {

        // =====================================================
        // ALERT DATA
        // =====================================================

        alertViewModel.alerts.observe(
            this
        ) { alerts ->


            // -------------------------------------------------
            // Update adapter
            // -------------------------------------------------

            alertAdapter.updateAlerts(
                alerts
            )


            // -------------------------------------------------
            // Find UI elements
            // -------------------------------------------------

            val recyclerAlerts =
                findViewById<RecyclerView>(
                    R.id.recyclerAlerts
                )

            val emptyState =
                findViewById<View>(
                    R.id.layoutEmptyAlerts
                )


            // -------------------------------------------------
            // Show / hide empty state
            // -------------------------------------------------

            if (alerts.isEmpty()) {

                recyclerAlerts.visibility =
                    View.GONE

                emptyState.visibility =
                    View.VISIBLE

            } else {

                recyclerAlerts.visibility =
                    View.VISIBLE

                emptyState.visibility =
                    View.GONE
            }
        }


        // =====================================================
        // FIREBASE ERROR
        // =====================================================

        alertViewModel.error.observe(
            this
        ) { errorMessage ->

            if (
                !errorMessage.isNullOrEmpty()
            ) {

                Toast.makeText(
                    this,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        // =====================================================
        // START FIREBASE LISTENER
        // =====================================================

        alertViewModel.observeAlerts()


        // =====================================================
        // MARK ALERTS AS READ
        // =====================================================
        //
        // When the user opens the Alerts screen, all currently
        // unread alerts are marked as read.
        //
        // This causes the red notification dot in the global
        // top bar to disappear when the user returns to another
        // screen.
        // =====================================================

        alertViewModel.markAllAlertsAsRead(

            onSuccess = {

                // No UI action required.
                //
                // Firebase has now changed:
                //
                // read: false
                //
                // to:
                //
                // read: true
            },

            onError = { message ->

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}