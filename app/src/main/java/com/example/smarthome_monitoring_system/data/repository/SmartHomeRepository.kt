package com.example.smarthome_monitoring_system.data.repository

import com.example.smarthome_monitoring_system.data.firebase.AlertFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.DeviceFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.FloorFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.SafetyFirebaseDataSource
import com.example.smarthome_monitoring_system.data.firebase.ScheduleFirebaseDataSource
import com.example.smarthome_monitoring_system.data.model.Alert
import com.example.smarthome_monitoring_system.data.model.Device
import com.example.smarthome_monitoring_system.data.model.DeviceSchedule
import com.example.smarthome_monitoring_system.data.model.Floor
import com.example.smarthome_monitoring_system.data.model.SafetyRuntime
import com.example.smarthome_monitoring_system.data.model.SafetySettings
import com.example.smarthome_monitoring_system.data.model.SwitchChannel

class SmartHomeRepository(
    private val floorFirebaseDataSource: FloorFirebaseDataSource,
    private val deviceFirebaseDataSource: DeviceFirebaseDataSource,
    private val scheduleFirebaseDataSource: ScheduleFirebaseDataSource =
        ScheduleFirebaseDataSource(),
    private val safetyFirebaseDataSource: SafetyFirebaseDataSource =
        SafetyFirebaseDataSource(),
    private val alertFirebaseDataSource: AlertFirebaseDataSource =
        AlertFirebaseDataSource()
) {

    // =========================================================
    // FLOOR
    // =========================================================

    fun observeFloors(
        onSuccess: (List<Floor>) -> Unit,
        onError: (String) -> Unit
    ) {
        floorFirebaseDataSource.observeFloors(
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun addFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        floorFirebaseDataSource.addFloor(
            floor = floor,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun updateFloor(
        floor: Floor,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        floorFirebaseDataSource.updateFloor(
            floor = floor,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun deleteFloor(
        floorId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        floorFirebaseDataSource.deleteFloor(
            floorId = floorId,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // =========================================================
    // DEVICE
    // =========================================================

    fun observeDevices(
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {
        deviceFirebaseDataSource.observeDevices(
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun observeDevicesByFloor(
        floorId: String,
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {
        deviceFirebaseDataSource.observeDevicesByFloor(
            floorId = floorId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun addDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        deviceFirebaseDataSource.addDevice(
            device = device,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun updateDevice(
        device: Device,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        deviceFirebaseDataSource.updateDevice(
            device = device,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun deleteDevice(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        deviceFirebaseDataSource.deleteDevice(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // =========================================================
    // MULTI-SWITCH CHILD CHANNELS
    // =========================================================

        fun observeSwitchChannels(
            deviceId: String,
            onSuccess: (List<SwitchChannel>) -> Unit,
            onError: (String) -> Unit
        ) {

            deviceFirebaseDataSource.observeSwitchChannels(

                deviceId = deviceId,

                onSuccess = onSuccess,

                onError = onError
            )
        }


        fun addSwitchChannel(
            deviceId: String,
            switchChannel: SwitchChannel,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {

            deviceFirebaseDataSource.addSwitchChannel(

                deviceId = deviceId,

                switchChannel = switchChannel,

                onSuccess = onSuccess,

                onError = onError
            )
        }


        fun updateSwitchChannel(
            deviceId: String,
            switchChannel: SwitchChannel,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {

            deviceFirebaseDataSource.updateSwitchChannel(

                deviceId = deviceId,

                switchChannel = switchChannel,

                onSuccess = onSuccess,

                onError = onError
            )
        }


        fun deleteSwitchChannel(
            deviceId: String,
            switchId: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {

            deviceFirebaseDataSource.deleteSwitchChannel(

                deviceId = deviceId,

                switchId = switchId,

                onSuccess = onSuccess,

                onError = onError
            )
        }


        fun turnOffAllChildSwitches(
            deviceId: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {

            deviceFirebaseDataSource.turnOffAllSwitches(
                deviceId = deviceId,
                onSuccess = onSuccess,
                onError = onError
            )
        }


    // =========================================================
    // SCHEDULE
    // =========================================================

    fun observeSchedule(
        deviceId: String,
        onSuccess: (DeviceSchedule?) -> Unit,
        onError: (String) -> Unit
    ) {
        scheduleFirebaseDataSource.observeSchedule(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun saveSchedule(
        schedule: DeviceSchedule,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scheduleFirebaseDataSource.saveSchedule(
            schedule = schedule,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun deleteSchedule(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scheduleFirebaseDataSource.deleteSchedule(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // =========================================================
    // SAFETY SETTINGS
    // =========================================================

    fun observeSafetySettings(
        deviceId: String,
        onSuccess: (SafetySettings?) -> Unit,
        onError: (String) -> Unit
    ) {
        safetyFirebaseDataSource.observeSafetySettings(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun saveSafetySettings(
        settings: SafetySettings,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        safetyFirebaseDataSource.saveSafetySettings(
            settings = settings,
            onSuccess = onSuccess,
            onError = onError
        )
    }


    // =========================================================
    // SAFETY RUNTIME
    // =========================================================

    fun observeSafetyRuntime(
        deviceId: String,
        onSuccess: (SafetyRuntime?) -> Unit,
        onError: (String) -> Unit
    ) {
        safetyFirebaseDataSource.observeSafetyRuntime(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun saveSafetyRuntime(
        runtime: SafetyRuntime,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        safetyFirebaseDataSource.saveSafetyRuntime(
            runtime = runtime,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun clearSafetyRuntime(
        deviceId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        safetyFirebaseDataSource.clearSafetyRuntime(
            deviceId = deviceId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    // =========================================================
    // ALERTS
    // =========================================================

        fun observeAlerts(
            onSuccess: (List<Alert>) -> Unit,
            onError: (String) -> Unit
        ) {

            alertFirebaseDataSource.observeAlerts(
                onSuccess = onSuccess,
                onError = onError
            )
        }

    // =========================================================
    // MARK ALL ALERTS AS READ
    // =========================================================

        fun markAllAlertsAsRead(
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {

            alertFirebaseDataSource.markAllAlertsAsRead(
                onSuccess = onSuccess,
                onError = onError
            )
        }
}