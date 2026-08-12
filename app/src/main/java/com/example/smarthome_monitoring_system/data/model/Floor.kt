package com.example.smarthome_monitoring_system.data.model

data class Floor(
    var id: String = "",
    var name: String = "",
    var floorPlanUrl: String = "",
    var gridRows: Int = 0,
    var gridColumns: Int = 0,
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
)