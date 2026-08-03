package com.example.smarthome_monitoring_system.data.model

data class Device(

    var id: String = "",

    var name: String = "",

    var floorId: String = "",

    var type: DeviceType = DeviceType.OUTLET,

    var status: DeviceStatus = DeviceStatus.OFF,

    var row: Int = 0,

    var column: Int = 0

)