package com.example.smarthome_monitoring_system.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome_monitoring_system.R
import com.example.smarthome_monitoring_system.data.model.StatCard

class StatCardAdapter(
    private val statCards: List<StatCard>,
    private val onCardClick: (StatCard) -> Unit
) : RecyclerView.Adapter<StatCardAdapter.StatCardViewHolder>() {

    class StatCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val statIcon: ImageView =
            itemView.findViewById(R.id.imageStatIcon)

        val statValue: TextView =
            itemView.findViewById(R.id.textStatValue)

        val statTitle: TextView =
            itemView.findViewById(R.id.textStatTitle)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatCardViewHolder {

        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_stat_card, parent, false)

        return StatCardViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: StatCardViewHolder,
        position: Int
    ) {
        val statCard = statCards[position]

        holder.statIcon.setImageResource(statCard.iconResource)
        holder.statIcon.setColorFilter(statCard.tintColor)
        
        val iconContainer = holder.itemView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardStatIconContainer)
        iconContainer.setCardBackgroundColor(statCard.backgroundColor)

        holder.statValue.text = statCard.value
        holder.statValue.setTextColor(statCard.tintColor)
        
        holder.statTitle.text = statCard.title

        holder.itemView.setOnClickListener {
            onCardClick(statCard)
        }
    }

    override fun getItemCount(): Int {
        return statCards.size
    }
}