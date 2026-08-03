package com.example.smarthome_monitoring_system.data.model

data class SafetyAlert(

    var id: String = "",

    var deviceId: String = "",

    var deviceName: String = "",

    var message: String = "",

    var createdAt: Long = 0L,

    var isRead: Boolean = false

)