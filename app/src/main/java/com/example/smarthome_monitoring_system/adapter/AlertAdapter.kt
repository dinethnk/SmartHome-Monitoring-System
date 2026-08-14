package com.example.smarthome_monitoring_system.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Alert
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertAdapter(
    private var alerts: List<Alert>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        private const val VIEW_TYPE_SECTION = 0
        private const val VIEW_TYPE_ALERT = 1
    }


    // =========================================================
    // DISPLAY ITEMS
    // =========================================================

    private sealed class DisplayItem {

        data class Section(
            val title: String
        ) : DisplayItem()

        data class AlertItem(
            val alert: Alert
        ) : DisplayItem()
    }


    private var displayItems =
        createDisplayItems(alerts)


    // =========================================================
    // SECTION VIEW HOLDER
    // =========================================================

    class SectionViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val title: TextView =
            itemView.findViewById(
                R.id.textSectionTitle
            )
    }


    // =========================================================
    // ALERT VIEW HOLDER
    // =========================================================

    class AlertViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val card: MaterialCardView =
            itemView as MaterialCardView

        val alertIcon: ImageView =
            itemView.findViewById(
                R.id.imageAlertIcon
            )

        val alertType: TextView =
            itemView.findViewById(
                R.id.textAlertType
            )

        val deviceName: TextView =
            itemView.findViewById(
                R.id.textAlertDeviceName
            )

        val message: TextView =
            itemView.findViewById(
                R.id.textAlertMessage
            )

        val time: TextView =
            itemView.findViewById(
                R.id.textAlertTime
            )
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_SECTION -> {

                val view =
                    LayoutInflater.from(
                        parent.context
                    ).inflate(
                        R.layout.item_alert_section,
                        parent,
                        false
                    )

                SectionViewHolder(view)
            }

            else -> {

                val view =
                    LayoutInflater.from(
                        parent.context
                    ).inflate(
                        R.layout.item_alert,
                        parent,
                        false
                    )

                AlertViewHolder(view)
            }
        }
    }


    // =========================================================
    // BIND
    // =========================================================

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (
            val item = displayItems[position]
        ) {

            is DisplayItem.Section -> {

                val sectionHolder =
                    holder as SectionViewHolder

                sectionHolder.title.text =
                    item.title
            }

            is DisplayItem.AlertItem -> {

                val alertHolder =
                    holder as AlertViewHolder

                bindAlert(
                    alertHolder,
                    item.alert
                )
            }
        }
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    override fun getItemCount(): Int =
        displayItems.size


    // =========================================================
    // VIEW TYPE
    // =========================================================

    override fun getItemViewType(
        position: Int
    ): Int {

        return when (
            displayItems[position]
        ) {

            is DisplayItem.Section ->
                VIEW_TYPE_SECTION

            is DisplayItem.AlertItem ->
                VIEW_TYPE_ALERT
        }
    }


    // =========================================================
    // UPDATE ALERTS
    // =========================================================

    fun updateAlerts(
        newAlerts: List<Alert>
    ) {

        alerts =
            newAlerts

        displayItems =
            createDisplayItems(
                alerts
            )

        notifyDataSetChanged()
    }


    // =========================================================
    // CREATE DISPLAY ITEMS
    // =========================================================

    private fun createDisplayItems(
        alerts: List<Alert>
    ): List<DisplayItem> {

        if (alerts.isEmpty()) {
            return emptyList()
        }

        val result =
            mutableListOf<DisplayItem>()

        val sortedAlerts =
            alerts.sortedByDescending {
                it.timestamp
            }


        // -----------------------------------------------------
        // SAFETY
        // -----------------------------------------------------

        val safetyAlerts =
            sortedAlerts.filter {
                it.type == "SAFETY_CUTOFF"
            }

        if (safetyAlerts.isNotEmpty()) {

            result.add(
                DisplayItem.Section(
                    "Safety"
                )
            )

            safetyAlerts.forEach {

                result.add(
                    DisplayItem.AlertItem(it)
                )
            }
        }


        // -----------------------------------------------------
        // TODAY
        // -----------------------------------------------------

        val activityAlerts =
            sortedAlerts.filter {
                it.type != "SAFETY_CUTOFF"
            }

        if (activityAlerts.isNotEmpty()) {

            result.add(
                DisplayItem.Section(
                    "Today"
                )
            )

            activityAlerts.forEach {

                result.add(
                    DisplayItem.AlertItem(it)
                )
            }
        }

        return result
    }


    // =========================================================
    // BIND ALERT
    // =========================================================

    private fun bindAlert(
        holder: AlertViewHolder,
        alert: Alert
    ) {

        holder.deviceName.text =
            alert.deviceName

        holder.message.text =
            alert.message

        holder.alertType.text =
            getAlertTypeLabel(
                alert.type
            )

        holder.alertIcon.setImageResource(
            getAlertIcon(
                alert.type
            )
        )

        holder.time.text =
            formatTimestamp(
                alert.timestamp
            )

        applyAlertColors(
            holder,
            alert.type
        )
    }


    // =========================================================
    // ALERT COLORS
    // =========================================================

    private fun applyAlertColors(
        holder: AlertViewHolder,
        type: String
    ) {

        val context =
            holder.itemView.context

        when (type) {

            // -------------------------------------------------
            // SAFETY CUTOFF
            // -------------------------------------------------

            "SAFETY_CUTOFF" -> {

                holder.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_safety_background
                    )
                )

                holder.alertType.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_safety_primary
                    )
                )

                holder.alertIcon.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.alert_safety_icon
                        )
                    )
            }


            // -------------------------------------------------
            // SCHEDULE ON
            // -------------------------------------------------

            "SCHEDULE_ON" -> {

                holder.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_schedule_on_background
                    )
                )

                holder.alertType.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_schedule_on_primary
                    )
                )

                holder.alertIcon.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.alert_schedule_on_icon
                        )
                    )
            }


            // -------------------------------------------------
            // SCHEDULE OFF
            // -------------------------------------------------

            "SCHEDULE_OFF" -> {

                holder.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_schedule_off_background
                    )
                )

                holder.alertType.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.alert_schedule_off_primary
                    )
                )

                holder.alertIcon.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.alert_schedule_off_icon
                        )
                    )
            }


            // -------------------------------------------------
            // OTHER
            // -------------------------------------------------

            else -> {

                holder.card.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.surface
                    )
                )

                holder.alertType.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary
                    )
                )
            }
        }
    }


    // =========================================================
    // ALERT TYPE
    // =========================================================

    private fun getAlertTypeLabel(
        type: String
    ): String {

        return when (type) {

            "SAFETY_CUTOFF" ->
                "SAFETY CUTOFF"

            "SCHEDULE_ON" ->
                "SCHEDULE ON"

            "SCHEDULE_OFF" ->
                "SCHEDULE OFF"

            else ->
                type
                    .replace(
                        "_",
                        " "
                    )
                    .uppercase()
        }
    }


    // =========================================================
    // ICON
    // =========================================================

    private fun getAlertIcon(
        type: String
    ): Int {

        return when (type) {

            "SAFETY_CUTOFF" ->
                R.drawable.ic_devices

            "SCHEDULE_ON",
            "SCHEDULE_OFF" ->
                R.drawable.ic_clock

            else ->
                R.drawable.ic_notifications
        }
    }


    // =========================================================
    // TIME
    // =========================================================

    private fun formatTimestamp(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {
            return ""
        }

        val formatter =
            SimpleDateFormat(
                "h:mm a",
                Locale.getDefault()
            )

        return formatter.format(
            Date(timestamp)
        )
    }
}