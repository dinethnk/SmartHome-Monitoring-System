package com.example.smarthome_monitoring_system.data.model

data class HomeEvent(
    var id: String = "",
    var deviceId: String = "",
    var deviceName: String = "",
    var type: String = "", // e.g., "POWER_ON", "POWER_OFF"
    var message: String = "",
    var timestamp: Long = 0L
)
