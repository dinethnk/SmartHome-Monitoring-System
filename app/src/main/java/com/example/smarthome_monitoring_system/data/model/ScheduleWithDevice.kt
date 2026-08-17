package com.example.smarthome_monitoring_system.data.model

data class ScheduleWithDevice(
    val device: Device,
    val schedule: DeviceSchedule,
    val floorName: String
)