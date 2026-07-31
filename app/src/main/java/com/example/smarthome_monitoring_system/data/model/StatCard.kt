package com.example.smarthome_monitoring_system.data.model

import androidx.annotation.DrawableRes

data class StatCard(
    val title: String,
    val value: String,
    @DrawableRes val iconResource: Int
)