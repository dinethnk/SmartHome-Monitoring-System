package com.example.smarthome_monitoring_system.data.model

data class SafetySettings(
    var deviceId: String = "",
    var enabled: Boolean = true,
    var maxOnDuration: Int = 30
)