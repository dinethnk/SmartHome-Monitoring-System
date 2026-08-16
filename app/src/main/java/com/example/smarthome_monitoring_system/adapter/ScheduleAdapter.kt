package com.example.smarthome_monitoring_system.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.ScheduleWithDevice

class ScheduleAdapter(
    private val schedules: List<ScheduleWithDevice>,
    private val onScheduleClick: (ScheduleWithDevice) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.textScheduleDeviceName)
        val timeRange: TextView = itemView.findViewById(R.id.textScheduleTimeRange)
        val status: TextView = itemView.findViewById(R.id.textScheduleStatus)
        val viewButton: ImageButton = itemView.findViewById(R.id.buttonViewSchedule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_card, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = schedules[position]
        
        holder.deviceName.text = item.device.name
        holder.timeRange.text = "${formatTime(item.schedule.onTime)} - ${formatTime(item.schedule.offTime)}"
        
        val isEnabled = item.schedule.enabled
        holder.status.text = if (isEnabled) "ACTIVE" else "DISABLED"
        holder.status.setTextColor(Color.parseColor(if (isEnabled) "#00BFA5" else "#9E9E9E"))
        holder.status.backgroundTintList = ColorStateList.valueOf(Color.parseColor(if (isEnabled) "#E0F2F1" else "#F5F5F5"))

        val listener = View.OnClickListener {
            onScheduleClick(item)
        }
        
        holder.itemView.setOnClickListener(listener)
        holder.viewButton.setOnClickListener(listener)
    }

    private fun formatTime(time: String): String {
        val parts = time.split(":")
        if (parts.size != 2) return time
        
        val hour = parts[0].toIntOrNull() ?: return time
        val minute = parts[1].toIntOrNull() ?: return time
        
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        
        return String.format("%02d:%02d %s", displayHour, minute, period)
    }

    override fun getItemCount(): Int = schedules.size
}