package com.example.smarthome_monitoring_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.UsageRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UsageRecordAdapter(
    private val usageRecords: List<UsageRecord>
) : RecyclerView.Adapter<
        UsageRecordAdapter.UsageRecordViewHolder
        >() {

    class UsageRecordViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val deviceIcon: ImageView =
            itemView.findViewById(
                R.id.imageUsageDeviceIcon
            )

        val deviceName: TextView =
            itemView.findViewById(
                R.id.textUsageDeviceName
            )

        val usageDuration: TextView =
            itemView.findViewById(
                R.id.textUsageDuration
            )

        val usageDateTime: TextView =
            itemView.findViewById(
                R.id.textUsageDateTime
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsageRecordViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_usage_record,
                parent,
                false
            )

        return UsageRecordViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: UsageRecordViewHolder,
        position: Int
    ) {
        val usageRecord =
            usageRecords[position]

        holder.deviceName.text =
            usageRecord.deviceName

        holder.usageDuration.text =
            "Used for ${usageRecord.durationMinutes} minutes"

        holder.usageDateTime.text =
            formatTimestamp(usageRecord.timestamp)

        holder.deviceIcon.setImageResource(
            getDeviceIcon(usageRecord.deviceName)
        )
    }

    private fun getDeviceIcon(
        deviceName: String
    ): Int {
        val normalizedName =
            deviceName.lowercase()

        return when {
            normalizedName.contains("light") ->
                R.drawable.ic_light

            normalizedName.contains("camera") ->
                R.drawable.ic_camera

            normalizedName.contains("iron") ->
                R.drawable.ic_iron

            else ->
                R.drawable.ic_power
        }
    }

    private fun formatTimestamp(
        timestamp: Long
    ): String {
        if (timestamp == 0L) {
            return "No date available"
        }

        val formatter =
            SimpleDateFormat(
                "dd MMM yyyy • hh:mm a",
                Locale.getDefault()
            )

        return formatter.format(
            Date(timestamp)
        )
    }

    override fun getItemCount(): Int {
        return usageRecords.size
    }
}