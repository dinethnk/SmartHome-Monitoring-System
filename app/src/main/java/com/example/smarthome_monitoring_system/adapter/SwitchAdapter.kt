package com.example.smarthome_monitoring_system.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.SwitchChannel
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class SwitchAdapter(
    private val switchChannels: List<SwitchChannel>,

    private val onSwitchChanged: (
        SwitchChannel
    ) -> Unit,

    private val onEditClicked: (
        SwitchChannel
    ) -> Unit,

    private val onDeleteClicked: (
        SwitchChannel
    ) -> Unit

) : RecyclerView.Adapter<SwitchAdapter.SwitchViewHolder>() {


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    class SwitchViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val switchName: TextView =
            itemView.findViewById(
                R.id.textSwitchName
            )

        val switchState: TextView =
            itemView.findViewById(
                R.id.textSwitchState
            )

        val switchControl: MaterialSwitch =
            itemView.findViewById(
                R.id.switchChannelControl
            )

        val editButton: MaterialButton =
            itemView.findViewById(
                R.id.buttonEditSwitch
            )

        val deleteButton: MaterialButton =
            itemView.findViewById(
                R.id.buttonDeleteSwitch
            )
    }


    // =========================================================
    // CREATE VIEW HOLDER
    // =========================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwitchViewHolder {

        val itemView =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_switch_control,
                    parent,
                    false
                )

        return SwitchViewHolder(
            itemView
        )
    }


    // =========================================================
    // BIND VIEW HOLDER
    // =========================================================

    override fun onBindViewHolder(
        holder: SwitchViewHolder,
        position: Int
    ) {

        val switchChannel =
            switchChannels[position]


        // -----------------------------------------------------
        // Name
        // -----------------------------------------------------

        holder.switchName.text =
            switchChannel.name


        // -----------------------------------------------------
        // Update current state
        // -----------------------------------------------------

        updateSwitchState(
            holder,
            switchChannel.isOn
        )


        // -----------------------------------------------------
        // Remove old listener
        //
        // Important because RecyclerView reuses views.
        // -----------------------------------------------------

        holder.switchControl
            .setOnCheckedChangeListener(null)


        // -----------------------------------------------------
        // Set Firebase/current value
        // -----------------------------------------------------

        holder.switchControl.isChecked =
            switchChannel.isOn


        // -----------------------------------------------------
        // Switch ON/OFF
        // -----------------------------------------------------

        holder.switchControl
            .setOnCheckedChangeListener {

                    _,
                    isChecked ->

                switchChannel.isOn =
                    isChecked


                updateSwitchState(
                    holder,
                    isChecked
                )


                onSwitchChanged(
                    switchChannel
                )
            }


        // -----------------------------------------------------
        // EDIT
        // -----------------------------------------------------

        holder.editButton.setOnClickListener {

            onEditClicked(
                switchChannel
            )
        }


        // -----------------------------------------------------
        // DELETE
        // -----------------------------------------------------

        holder.deleteButton.setOnClickListener {

            onDeleteClicked(
                switchChannel
            )
        }
    }


    // =========================================================
    // UPDATE STATE UI
    // =========================================================

    private fun updateSwitchState(
        holder: SwitchViewHolder,
        isOn: Boolean
    ) {

        if (isOn) {

            holder.switchState.text =
                "ON"

            holder.switchState.setTextColor(
                Color.parseColor(
                    "#00866A"
                )
            )

        } else {

            holder.switchState.text =
                "OFF"

            holder.switchState.setTextColor(
                Color.parseColor(
                    "#716B76"
                )
            )
        }
    }


    // =========================================================
    // ITEM COUNT
    // =========================================================

    override fun getItemCount(): Int {

        return switchChannels.size
    }
}