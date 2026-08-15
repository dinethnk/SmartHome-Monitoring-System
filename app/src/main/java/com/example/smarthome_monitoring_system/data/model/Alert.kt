package com.example.smarthome_monitoring_system.data.model

data class Alert(

    var id: String = "",

    var deviceId: String = "",

    var deviceName: String = "",

    var message: String = "",

    var read: Boolean = false,

    var timestamp: Long = 0L,

    var type: String = ""
)