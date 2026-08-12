package com.example.smarthome_monitoring_system.data.model

data class UsageRecord(
    var id: String = "",
    var deviceId: String = "",
    var deviceName: String = "",
    var durationMinutes: Int = 0,
    var timestamp: Long = 0L
)

//data class UsageRecord(
//
//    var id: String = "",
//
//    var deviceId: String = "",
//
//    var deviceName: String = "",
//
//    var startTime: Long = 0L,
//
//    var endTime: Long = 0L,
//
//    var durationMinutes: Long = 0L,
//
//    var shutdownReason: ShutdownReason = ShutdownReason.MANUAL
//
//)