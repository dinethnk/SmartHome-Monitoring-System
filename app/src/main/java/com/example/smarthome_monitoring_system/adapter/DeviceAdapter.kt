package com.example.smarthome_monitoring_system.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.DeviceType
import com.example.smarthome_monitoring_system.data.model.Floor

class DeviceAdapter(
    private val devices: List<Device>,
    private val floors: List<Floor>,
    private val onDeviceClick: (Device, String) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textDeviceName)
        val location: TextView = itemView.findViewById(R.id.textDeviceLocation)
        val status: TextView = itemView.findViewById(R.id.textDeviceStatus)
        val icon: ImageView = itemView.findViewById(R.id.imageDeviceIcon)
        val viewButton: ImageButton = itemView.findViewById(R.id.buttonViewDevice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device_card, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        val floor = floors.find { it.id == device.floorId }
        val floorName = floor?.name ?: "Unknown Floor"

        holder.name.text = device.name
        holder.location.text = floorName
        holder.icon.setImageResource(getDeviceIcon(device.type))

        val isOn = device.status == DeviceStatus.ON
        holder.status.text = if (isOn) "ON" else "OFF"
        holder.status.setTextColor(Color.parseColor(if (isOn) "#00BFA5" else "#9E9E9E"))
        holder.status.backgroundTintList = ColorStateList.valueOf(Color.parseColor(if (isOn) "#E0F2F1" else "#F5F5F5"))

        val listener = View.OnClickListener {
            onDeviceClick(device, floorName)
        }
        
        holder.itemView.setOnClickListener(listener)
        holder.viewButton.setOnClickListener(listener)
    }

    private fun getDeviceIcon(type: DeviceType): Int {
        return when (type) {
            DeviceType.LIGHT -> R.drawable.ic_light
            DeviceType.OUTLET -> R.drawable.ic_devices
            DeviceType.MULTI_SWITCH -> R.drawable.ic_multi_switch
            DeviceType.SAFETY_DEVICE -> R.drawable.ic_iron
            DeviceType.CAMERA -> R.drawable.ic_camera
        }
    }

    override fun getItemCount(): Int = devices.size
}