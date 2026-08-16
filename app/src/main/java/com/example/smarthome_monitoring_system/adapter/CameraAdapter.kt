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
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceStatus
import com.example.smarthome_monitoring_system.data.model.Floor

class CameraAdapter(
    private val cameras: List<Device>,
    private val floors: List<Floor>,
    private val onCameraClick: (Device, String) -> Unit
) : RecyclerView.Adapter<CameraAdapter.CameraViewHolder>() {

    class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textCameraName)
        val location: TextView = itemView.findViewById(R.id.textCameraLocation)
        val status: TextView = itemView.findViewById(R.id.textCameraStatus)
        val viewButton: ImageButton = itemView.findViewById(R.id.buttonViewCamera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera_card, parent, false)
        return CameraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CameraViewHolder, position: Int) {
        val camera = cameras[position]
        val floor = floors.find { it.id == camera.floorId }
        val floorName = floor?.name ?: "Unknown Floor"

        holder.name.text = camera.name
        holder.location.text = floorName

        val isOn = camera.status == DeviceStatus.ON
        holder.status.text = if (isOn) "LIVE" else "OFF"
        holder.status.setTextColor(Color.parseColor(if (isOn) "#00BFA5" else "#9E9E9E"))
        holder.status.backgroundTintList = ColorStateList.valueOf(Color.parseColor(if (isOn) "#E0F2F1" else "#F5F5F5"))

        val listener = View.OnClickListener {
            onCameraClick(camera, floorName)
        }
        
        holder.itemView.setOnClickListener(listener)
        holder.viewButton.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = cameras.size
}