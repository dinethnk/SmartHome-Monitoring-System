import {
    ref,
    onValue,
    update
} from "https://www.gstatic.com/firebasejs/12.17.1/firebase-database.js";

import {
    database
} from "./firebase-config.js";

/*
 * Monitor Firebase connection.
 */
export function monitorFirebaseConnection(callback) {
    const connectionReference =
        ref(database, ".info/connected");

    return onValue(
        connectionReference,

        (snapshot) => {
            callback(snapshot.val() === true);
        },

        (error) => {
            console.error(
                "Firebase connection listener failed:",
                error
            );

            callback(false);
        }
    );
}

/*
 * Listen to floors.
 */
export function listenToFloors(
    onFloorsChanged,
    onError
) {
    const floorsReference =
        ref(database, "floors");

    return onValue(
        floorsReference,

        (snapshot) => {
            const floorsData =
                snapshot.val() || {};

            const floors =
                Object.entries(floorsData).map(
                    ([floorId, floor]) => ({
                        id: floorId,
                        ...floor
                    })
                );

            onFloorsChanged(floors);
        },

        (error) => {
            console.error(
                "Unable to read floors:",
                error
            );

            if (onError) {
                onError(error);
            }
        }
    );
}

/*
 * Listen to devices.
 */
export function listenToDevices(
    onDevicesChanged,
    onError
) {
    const devicesReference =
        ref(database, "devices");

    return onValue(
        devicesReference,

        (snapshot) => {
            const devicesData =
                snapshot.val() || {};

            const devices =
                Object.entries(devicesData).map(
                    ([deviceId, device]) => ({
                        id: deviceId,
                        ...device
                    })
                );

            onDevicesChanged(devices);
        },

        (error) => {
            console.error(
                "Unable to read devices:",
                error
            );

            if (onError) {
                onError(error);
            }
        }
    );
}

/*
 * Update device state from the simulator.
 */
export async function updateDeviceStatus(
    deviceId,
    newStatus
) {
    const deviceReference =
        ref(
            database,
            `devices/${deviceId}`
        );

    await update(
        deviceReference,
        {
            status: newStatus,
            lastUpdatedBy: "SIMULATOR",
            lastUpdatedAt: Date.now()
        }
    );
}

export async function updateMultiSwitchPoint(
    deviceId,
    switchId,
    newState
) {
    const switchReference = ref(
        database,
        `devices/${deviceId}/switches/${switchId}`
    );

    await update(switchReference, {
        isOn: newState,
        on: newState
    });
}