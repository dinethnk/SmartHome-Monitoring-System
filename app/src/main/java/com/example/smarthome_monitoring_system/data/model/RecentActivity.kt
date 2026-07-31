package com.example.smarthome_monitoring_system.data.model

import androidx.annotation.DrawableRes

data class RecentActivity(
    val title: String,
    val description: String,
    val time: String,
    @DrawableRes val iconResource: Int
)