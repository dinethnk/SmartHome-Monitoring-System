const floorFilter =
    document.getElementById("floorFilter");

const deviceGrid =
    document.getElementById("deviceGrid");

const deviceCardTemplate =
    document.getElementById("deviceCardTemplate");

const loadingState =
    document.getElementById("loadingState");

const emptyState =
    document.getElementById("emptyState");

const errorState =
    document.getElementById("errorState");

const totalDeviceCount =
    document.getElementById("totalDeviceCount");

const onDeviceCount =
    document.getElementById("onDeviceCount");

const offDeviceCount =
    document.getElementById("offDeviceCount");

const problemDeviceCount =
    document.getElementById("problemDeviceCount");

/*
 * Add Firebase floors to the selector.
 */
export function displayFloorOptions(floors) {
    const previousSelection =
        floorFilter.value;

    floorFilter.innerHTML = "";

    const allFloorsOption =
        document.createElement("option");

    allFloorsOption.value = "all";
    allFloorsOption.textContent = "All Floors";

    floorFilter.appendChild(allFloorsOption);

    floors.forEach((floor) => {
        const option =
            document.createElement("option");

        option.value = floor.id;

        option.textContent =
            floor.name ||
            floor.floorName ||
            floor.id;

        floorFilter.appendChild(option);
    });

    const selectionStillExists =
        Array.from(floorFilter.options).some(
            (option) =>
                option.value === previousSelection
        );

    floorFilter.value =
        selectionStillExists
            ? previousSelection
            : "all";
}

/*
 * Detect floor selection changes.
 */
export function onFloorSelectionChanged(callback) {
    floorFilter.addEventListener(
        "change",
        () => callback(floorFilter.value)
    );
}

/*
 * Convert different database state formats into
 * ON, OFF, ERROR or DISCONNECTED.
 */
function getDeviceStatus(device) {
    const storedStatus =
        device.status ??
        device.state ??
        device.deviceStatus;

    if (storedStatus === true) {
        return "ON";
    }

    if (storedStatus === false) {
        return "OFF";
    }

    if (storedStatus === undefined ||
        storedStatus === null) {
        return "DISCONNECTED";
    }

    const status =
        String(storedStatus).toUpperCase();

    if (
        status === "ON" ||
        status === "OFF" ||
        status === "ERROR" ||
        status === "DISCONNECTED"
    ) {
        return status;
    }

    return "DISCONNECTED";
}

/*
 * Return the device's floor ID.
 */
export function getDeviceFloorId(device) {
    return String(
        device.floorId ??
        device.floorID ??
        device.floor_id ??
        device.floor ??
        ""
    );
}

/*
 * Find the readable name of a floor.
 */
function getFloorName(device, floors) {
    const floorId =
        getDeviceFloorId(device);

    const matchingFloor =
        floors.find(
            (floor) =>
                String(floor.id) === floorId
        );

    return (
        matchingFloor?.name ||
        matchingFloor?.floorName ||
        device.floorName ||
        floorId ||
        "Unknown floor"
    );
}

/*
 * Return an icon symbol according to the device type.
 */
function getDeviceIcon(type) {
    switch (type) {
        case "OUTLET":
            return "⚡";

        case "LIGHT":
            return "💡";

        case "MULTI_SWITCH":
        case "MULTI-SWITCH":
            return "🎚";

        case "SAFETY_DEVICE":
        case "IRON":
            return "♨";

        case "CAMERA":
            return "📷";

        default:
            return "●";
    }
}

/*
 * Apply the correct class to the status badge.
 */
function updateStatusBadge(
    badge,
    status
) {
    badge.textContent = status;

    badge.classList.remove(
        "status-on",
        "status-off",
        "status-error",
        "status-disconnected"
    );

    switch (status) {
        case "ON":
            badge.classList.add("status-on");
            break;

        case "OFF":
            badge.classList.add("status-off");
            break;

        case "ERROR":
            badge.classList.add("status-error");
            break;

        default:
            badge.classList.add(
                "status-disconnected"
            );
    }
}

/*
 * Display additional information in the control area.
 */
function displayDeviceDetails(
    container,
    device,
    status
) {
    const row =
        document.createElement("div");

    row.className = "control-row";

    const label =
        document.createElement("div");

    label.className = "control-label";

    const title =
        document.createElement("strong");

    title.textContent = "Current hardware state";

    const description =
        document.createElement("span");

    description.textContent =
        "Live value received from Firebase";

    label.append(title, description);

    const value =
        document.createElement("strong");

    value.textContent = status;

    row.append(label, value);

    container.appendChild(row);

    /*
     * Cameras and multi-switch units use specialized
     * controls, so do not display the simple toggle.
     */
    const deviceType = String(
        device.type ||
        device.deviceType ||
        "UNKNOWN"
    ).toUpperCase();

    const usesSimplePowerControl =
        deviceType === "OUTLET" ||
        deviceType === "LIGHT" ||
        deviceType === "IRON" ||
        deviceType === "SAFETY_DEVICE" ||
        deviceType === "SAFETY-DEVICE";

    if (usesSimplePowerControl) {
        const powerRow =
            document.createElement("div");

        powerRow.className = "control-row";

        const powerLabel =
            document.createElement("div");

        powerLabel.className = "control-label";

        const powerTitle =
            document.createElement("strong");

        powerTitle.textContent =
            "Hardware power";

        const powerDescription =
            document.createElement("span");

        powerDescription.textContent =
            "Simulate the physical ON/OFF switch";

        powerLabel.append(
            powerTitle,
            powerDescription
        );

        const toggleLabel =
            document.createElement("label");

        toggleLabel.className = "toggle";

        const toggleInput =
            document.createElement("input");

        toggleInput.type = "checkbox";
        toggleInput.className =
            "device-power-toggle";

        toggleInput.dataset.deviceId =
            device.id;

        toggleInput.checked =
            status === "ON";

        /*
         * Do not allow changes while the simulated
         * hardware is unavailable.
         */
        toggleInput.disabled =
            status === "ERROR" ||
            status === "DISCONNECTED";

        const toggleSlider =
            document.createElement("span");

        toggleSlider.className =
            "toggle-slider";

        toggleLabel.append(
            toggleInput,
            toggleSlider
        );

        powerRow.append(
            powerLabel,
            toggleLabel
        );

        container.appendChild(powerRow);
    }

    /*
     * Display maximum duration when available.
     */
    const maximumDuration =
        device.maxOnDuration ??
        device.max_on_duration;

    if (maximumDuration !== undefined) {
        const durationRow =
            document.createElement("div");

        durationRow.className = "control-row";

        durationRow.textContent =
            `Maximum ON duration: ` +
            `${maximumDuration} minutes`;

        container.appendChild(durationRow);
    }

    /*
     * Display schedule information when available.
     */
    if (device.scheduleEnabled === true) {
        const scheduleRow =
            document.createElement("div");

        scheduleRow.className = "control-row";

        const onTime =
            device.onTime || "Not set";

        const offTime =
            device.offTime || "Not set";

        scheduleRow.textContent =
            `Schedule: ${onTime} - ${offTime}`;

        container.appendChild(scheduleRow);
    }
}

/*
 * Display devices and update summary cards.
 */
export function displayDevices(
    devices,
    floors
) {
    loadingState.classList.add("hidden");
    errorState.classList.add("hidden");

    deviceGrid.innerHTML = "";

    updateSummaryCards(devices);

    if (devices.length === 0) {
        emptyState.classList.remove("hidden");
        return;
    }

    emptyState.classList.add("hidden");

    devices.forEach((device) => {
        const cardFragment =
            deviceCardTemplate.content.cloneNode(true);

        const card =
            cardFragment.querySelector(
                ".device-card"
            );

        const name =
            device.name ||
            device.deviceName ||
            device.id;

        const type = String(
            device.type ||
            device.deviceType ||
            "UNKNOWN"
        ).toUpperCase();

        const status =
            getDeviceStatus(device);

        const connected =
            device.connected ??
            device.isConnected ??
            status !== "DISCONNECTED";

        card.dataset.deviceId = device.id;

        card.querySelector(
            ".device-name"
        ).textContent = name;

        card.querySelector(
            ".device-location"
        ).textContent =
            getFloorName(device, floors);

        card.querySelector(
            ".device-type"
        ).textContent = type;

        card.querySelector(
            ".device-connection"
        ).textContent =
            connected
                ? "Connected"
                : "Disconnected";

        card.querySelector(
            ".device-icon-symbol"
        ).textContent =
            getDeviceIcon(type);

        const badge =
            card.querySelector(".status-badge");

        updateStatusBadge(
            badge,
            status
        );

        const controls =
            card.querySelector(
                ".device-controls"
            );

        displayDeviceDetails(
            controls,
            device,
            status
        );

        deviceGrid.appendChild(
            cardFragment
        );
    });
}

/*
 * Update the dashboard totals using the currently
 * displayed devices.
 */
function updateSummaryCards(devices) {
    let onCount = 0;
    let offCount = 0;
    let problemCount = 0;

    devices.forEach((device) => {
        const status =
            getDeviceStatus(device);

        if (status === "ON") {
            onCount++;
        } else if (status === "OFF") {
            offCount++;
        } else {
            problemCount++;
        }
    });

    totalDeviceCount.textContent =
        devices.length;

    onDeviceCount.textContent =
        onCount;

    offDeviceCount.textContent =
        offCount;

    problemDeviceCount.textContent =
        problemCount;
}