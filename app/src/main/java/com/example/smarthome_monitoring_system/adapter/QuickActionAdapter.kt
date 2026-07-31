package com.example.smarthome_monitoring_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.QuickAction

class QuickActionAdapter(
    private val quickActions: List<QuickAction>,
    private val onQuickActionClick: (QuickAction) -> Unit
) : RecyclerView.Adapter<QuickActionAdapter.QuickActionViewHolder>() {

    class QuickActionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val actionIcon: ImageView =
            itemView.findViewById(R.id.imageQuickActionIcon)

        val actionTitle: TextView =
            itemView.findViewById(R.id.textQuickActionTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuickActionViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_quick_action, parent, false)

        return QuickActionViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: QuickActionViewHolder,
        position: Int
    ) {
        val quickAction = quickActions[position]

        holder.actionIcon.setImageResource(
            quickAction.iconResource
        )

        holder.actionIcon.contentDescription =
            quickAction.title

        holder.actionTitle.text =
            quickAction.title

        holder.itemView.setOnClickListener {
            onQuickActionClick(quickAction)
        }
    }

    override fun getItemCount(): Int {
        return quickActions.size
    }
}