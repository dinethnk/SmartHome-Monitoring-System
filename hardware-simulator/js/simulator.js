import {
    monitorFirebaseConnection,
    listenToFloors,
    listenToDevices,
    updateDeviceStatus
} from "./firebase-service.js";

import {
    displayFloorOptions,
    displayDevices,
    getDeviceFloorId,
    onFloorSelectionChanged
} from "./ui.js";

const connectionStatus =
    document.getElementById("connectionStatus");

const connectionStatusText =
    document.getElementById(
        "connectionStatusText"
    );

const loadingState =
    document.getElementById("loadingState");

const errorState =
    document.getElementById("errorState");

const errorMessage =
    document.getElementById("errorMessage");

const refreshButton =
    document.getElementById("refreshButton");

const deviceGrid =
    document.getElementById("deviceGrid");

/*
 * Keep the latest Firebase data in memory.
 */
let allFloors = [];
let allDevices = [];
let selectedFloorId = "all";

/*
 * Update the connection indicator.
 */
function updateConnectionStatus(connected) {
    connectionStatus.classList.remove(
        "connecting",
        "connected",
        "disconnected"
    );

    if (connected) {
        connectionStatus.classList.add(
            "connected"
        );

        connectionStatusText.textContent =
            "Firebase Connected";
    } else {
        connectionStatus.classList.add(
            "disconnected"
        );

        connectionStatusText.textContent =
            "Firebase Disconnected";
    }
}

/*
 * Filter and display devices.
 */
function renderSelectedFloor() {
    let visibleDevices = allDevices;

    if (selectedFloorId !== "all") {
        visibleDevices =
            allDevices.filter(
                (device) =>
                    getDeviceFloorId(device) ===
                    selectedFloorId
            );
    }

    displayDevices(
        visibleDevices,
        allFloors
    );
}

/*
 * Display database errors.
 */
function showDatabaseError(error) {
    loadingState.classList.add("hidden");
    errorState.classList.remove("hidden");

    errorMessage.textContent =
        error.message ||
        "Unable to read Firebase.";
}

/*
 * Monitor Firebase connection.
 */
monitorFirebaseConnection(
    updateConnectionStatus
);

/*
 * Listen to floors.
 */
listenToFloors(
    (floors) => {
        allFloors = floors;

        displayFloorOptions(allFloors);
        renderSelectedFloor();

        console.log(
            "Floors received:",
            allFloors
        );
    },
    showDatabaseError
);

/*
 * Listen to devices.
 */
listenToDevices(
    (devices) => {
        allDevices = devices;

        renderSelectedFloor();

        console.log(
            "Devices received:",
            allDevices
        );
    },
    showDatabaseError
);

/*
 * React when a floor is selected.
 */
onFloorSelectionChanged(
    (newFloorId) => {
        selectedFloorId = newFloorId;

        renderSelectedFloor();
    }
);

/*
 * Manually refresh the browser page.
 */
refreshButton.addEventListener(
    "click",
    () => {
        window.location.reload();
    }
);

/*
 * Handle ON/OFF changes made in the simulator.
 */
deviceGrid.addEventListener(
    "change",

    async (event) => {
        const toggle =
            event.target.closest(
                ".device-power-toggle"
            );

        if (!toggle) {
            return;
        }

        const deviceId =
            toggle.dataset.deviceId;

        const previousStatus =
            toggle.checked ? "OFF" : "ON";

        const newStatus =
            toggle.checked ? "ON" : "OFF";

        toggle.disabled = true;

        try {
            await updateDeviceStatus(
                deviceId,
                newStatus
            );

            console.log(
                `${deviceId} changed to ${newStatus}`
            );

            /*
             * The Firebase listener will automatically
             * redraw the device card.
             */
        } catch (error) {
            console.error(
                "Unable to update device:",
                error
            );

            toggle.checked =
                previousStatus === "ON";

            toggle.disabled = false;

            alert(
                "Unable to update the device in Firebase."
            );
        }
    }
);