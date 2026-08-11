package com.example.smarthome_monitoring_system.adapter

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
    private val onFloorClick: (Floor) -> Unit,
    private val onEditFloor: (Floor) -> Unit,
    private val onDeleteFloor: (Floor) -> Unit
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

        val editButton: ImageButton =
            itemView.findViewById(R.id.buttonEditFloor)

        val deleteButton: ImageButton =
            itemView.findViewById(R.id.buttonDeleteFloor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FloorViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_floor,
                parent,
                false
            )

        return FloorViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: FloorViewHolder,
        position: Int
    ) {

        val floor = floors[position]

        // -----------------------------------------------------
        // Floor information
        // -----------------------------------------------------

        holder.floorName.text = floor.name

        /*
         * Device count will be calculated from Firebase
         * devices using floorId.
         *
         * For now, the floor object itself does not contain
         * deviceCount.
         */
        holder.deviceCount.text =
            "Loading devices..."

        /*
         * The current Firebase floor schema does not contain
         * an active/inactive field.
         */
        holder.floorStatus.text =
            "ACTIVE"


        // -----------------------------------------------------
        // Open floor
        // -----------------------------------------------------

        holder.itemView.setOnClickListener {
            onFloorClick(floor)
        }

        holder.openButton.setOnClickListener {
            onFloorClick(floor)
        }


        // -----------------------------------------------------
        // Edit floor
        // -----------------------------------------------------

        holder.editButton.setOnClickListener {
            onEditFloor(floor)
        }


        // -----------------------------------------------------
        // Delete floor
        // -----------------------------------------------------

        holder.deleteButton.setOnClickListener {
            onDeleteFloor(floor)
        }
    }

    override fun getItemCount(): Int {
        return floors.size
    }
}