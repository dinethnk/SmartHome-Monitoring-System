package com.example.smarthome_monitoring_system.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.SwitchChannel
import com.google.android.material.materialswitch.MaterialSwitch

class SwitchAdapter(
    private val switchChannels: List<SwitchChannel>,
    private val onSwitchChanged: (
        SwitchChannel,
        Boolean
    ) -> Unit
) : RecyclerView.Adapter<SwitchAdapter.SwitchViewHolder>() {

    class SwitchViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val switchName: TextView =
            itemView.findViewById(R.id.textSwitchName)

        val switchState: TextView =
            itemView.findViewById(R.id.textSwitchState)

        val switchControl: MaterialSwitch =
            itemView.findViewById(
                R.id.switchChannelControl
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwitchViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_switch_control,
                parent,
                false
            )

        return SwitchViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: SwitchViewHolder,
        position: Int
    ) {
        val switchChannel =
            switchChannels[position]

        holder.switchName.text =
            switchChannel.name

        updateSwitchState(
            holder,
            switchChannel.isOn
        )

        // Remove the old listener before changing isChecked.
        holder.switchControl.setOnCheckedChangeListener(null)

        holder.switchControl.isChecked =
            switchChannel.isOn

        holder.switchControl.setOnCheckedChangeListener {
                _,
                isChecked ->

            switchChannel.isOn = isChecked

            updateSwitchState(
                holder,
                isChecked
            )

            onSwitchChanged(
                switchChannel,
                isChecked
            )
        }
    }

    private fun updateSwitchState(
        holder: SwitchViewHolder,
        isOn: Boolean
    ) {
        if (isOn) {
            holder.switchState.text = "ON"

            holder.switchState.setTextColor(
                Color.parseColor("#00866A")
            )
        } else {
            holder.switchState.text = "OFF"

            holder.switchState.setTextColor(
                Color.parseColor("#716B76")
            )
        }
    }

    override fun getItemCount(): Int {
        return switchChannels.size
    }
}