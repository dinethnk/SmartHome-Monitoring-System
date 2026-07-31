package com.example.smarthome_monitoring_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.RecentActivity

class RecentActivityAdapter(
    private val activities: List<RecentActivity>
) : RecyclerView.Adapter<RecentActivityAdapter.RecentActivityViewHolder>() {

    class RecentActivityViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val activityIcon: ImageView =
            itemView.findViewById(R.id.imageActivityIcon)

        val activityTitle: TextView =
            itemView.findViewById(R.id.textActivityTitle)

        val activityDescription: TextView =
            itemView.findViewById(R.id.textActivityDescription)

        val activityTime: TextView =
            itemView.findViewById(R.id.textActivityTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentActivityViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)

        return RecentActivityViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: RecentActivityViewHolder,
        position: Int
    ) {
        val recentActivity = activities[position]

        holder.activityIcon.setImageResource(
            recentActivity.iconResource
        )

        holder.activityIcon.contentDescription =
            recentActivity.title

        holder.activityTitle.text =
            recentActivity.title

        holder.activityDescription.text =
            recentActivity.description

        holder.activityTime.text =
            recentActivity.time
    }

    override fun getItemCount(): Int {
        return activities.size
    }
}