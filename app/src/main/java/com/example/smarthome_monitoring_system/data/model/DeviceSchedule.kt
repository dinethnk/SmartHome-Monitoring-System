package com.example.smarthome_monitoring_system.data.model

data class DeviceSchedule(

    var deviceId: String = "",

    var enabled: Boolean = false,

    var onTime: String = "",

    var offTime: String = ""

)