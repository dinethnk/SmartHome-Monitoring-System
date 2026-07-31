package com.example.smarthome_monitoring_system.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.Floor

class FloorAdapter(
    private val floors: List<Floor>,
    private val onFloorClick: (Floor) -> Unit
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    class FloorViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val floorImage: ImageView =
            itemView.findViewById(R.id.imageFloor)

        val floorName: TextView =
            itemView.findViewById(R.id.textFloorItemName)

        val deviceCount: TextView =
            itemView.findViewById(R.id.textFloorItemDeviceCount)

        val floorStatus: TextView =
            itemView.findViewById(R.id.textFloorItemStatus)

        val openButton: ImageButton =
            itemView.findViewById(R.id.buttonOpenFloor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FloorViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_floor, parent, false)

        return FloorViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: FloorViewHolder,
        position: Int
    ) {
        val floor = floors[position]

        holder.floorName.text = floor.name

        holder.deviceCount.text =
            "${floor.deviceCount} connected devices"

        if (floor.active) {
            holder.floorStatus.text = "ACTIVE"
            holder.floorStatus.setTextColor(
                Color.parseColor("#00866A")
            )
        } else {
            holder.floorStatus.text = "INACTIVE"
            holder.floorStatus.setTextColor(
                Color.parseColor("#77727D")
            )
        }

        holder.itemView.setOnClickListener {
            onFloorClick(floor)
        }

        holder.openButton.setOnClickListener {
            onFloorClick(floor)
        }
    }

    override fun getItemCount(): Int {
        return floors.size
    }
}