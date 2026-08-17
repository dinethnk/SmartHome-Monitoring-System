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

    if (
        storedStatus === undefined ||
        storedStatus === null
    ) {
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
        case "SAFETY-DEVICE":
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


/**
 * Prevent text received from Firebase from being
 * interpreted as HTML.
 */
function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/**
 * Convert a multi-switch's switches into a normal
 * JavaScript array.
 *
 * This supports:
 * 1. Firebase objects using generated IDs.
 * 2. Normal JavaScript arrays.
 */
function getMultiSwitchPoints(device) {
    const switches =
        device.switches;

    if (!switches) {
        return [];
    }

    /*
     * Support a real JavaScript array.
     */
    if (Array.isArray(switches)) {
        return switches
            .filter(Boolean)
            .map((switchPoint, index) => ({
                id:
                    switchPoint.id ??
                    String(index),

                ...switchPoint
            }));
    }

    /*
     * Support the existing Firebase object format.
     */
    return Object.entries(switches).map(
        ([firebaseId, switchPoint]) => ({
            id:
                switchPoint.id ??
                firebaseId,

            ...switchPoint
        })
    );
}


/**
 * Create the internal power-outlet controls for a
 * MULTI_SWITCH device.
 */
export function renderMultiSwitchPoints(
    deviceId,
    device
) {
    const deviceType = String(
        device.type ||
        device.deviceType ||
        ""
    ).toUpperCase();

    if (
        deviceType !== "MULTI_SWITCH" &&
        deviceType !== "MULTI-SWITCH"
    ) {
        return "";
    }

    const switchPoints =
        getMultiSwitchPoints(device);

    /*
     * Some MULTI_SWITCH devices in Firebase do not
     * currently contain a switches collection.
     */
    if (switchPoints.length === 0) {
        return `
            <section class="multi-switch-section">
                <div class="multi-switch-section__header">
                    <span>Power outlets</span>

                    <span class="multi-switch-section__count">
                        0
                    </span>
                </div>

                <p class="multi-switch-empty">
                    No power outlets are configured.
                </p>
            </section>
        `;
    }

    const isMasterOn =
        getDeviceStatus(device) === "ON";

    const outletRows = switchPoints
        .map((switchPoint, index) => {
            /*
             * The existing Firebase records contain
             * both isOn and on fields.
             */
            const isOutletOn =
                switchPoint.isOn ??
                switchPoint.on ??
                false;

            const outletName =
                switchPoint.name ||
                `Outlet ${index + 1}`;

            const outletStatusText =
                isMasterOn
                    ? isOutletOn
                        ? "Power ON"
                        : "Power OFF"
                    : "Master switch is OFF";

            return `
                <div class="
                    multi-switch-outlet
                    ${isMasterOn ? "" : "is-disabled"}
                ">
                    <div class="multi-switch-outlet__details">
                        <div class="multi-switch-outlet__icon">
                            ⚡
                        </div>

                        <div class="multi-switch-outlet__text">
                            <div class="multi-switch-outlet__name">
                                ${escapeHtml(outletName)}
                            </div>

                            <div class="multi-switch-outlet__status">
                                ${outletStatusText}
                            </div>
                        </div>
                    </div>

                    <label class="switch-control">
                        <input
                            type="checkbox"
                            class="multi-switch-point-toggle"
                            data-device-id="${escapeHtml(deviceId)}"
                            data-switch-id="${escapeHtml(switchPoint.id)}"
                            ${isOutletOn ? "checked" : ""}
                            ${isMasterOn ? "" : "disabled"}
                            aria-label="Toggle ${escapeHtml(outletName)}"
                        />

                        <span class="switch-control__slider"></span>
                    </label>
                </div>
            `;
        })
        .join("");

    return `
        <section class="multi-switch-section">
            <div class="multi-switch-section__header">
                <span>Power outlets</span>

                <span class="multi-switch-section__count">
                    ${switchPoints.length}
                </span>
            </div>

            <div class="multi-switch-outlet-list">
                ${outletRows}
            </div>
        </section>
    `;
}


/*
 * Display additional information in the device
 * control area.
 */
function displayDeviceDetails(
    container,
    device,
    status
) {
    /*
     * Current hardware-state row.
     */
    const row =
        document.createElement("div");

    row.className = "control-row";

    const label =
        document.createElement("div");

    label.className = "control-label";

    const title =
        document.createElement("strong");

    title.textContent =
        "Current hardware state";

    const description =
        document.createElement("span");

    description.textContent =
        "Live value received from Firebase";

    label.append(
        title,
        description
    );

    const value =
        document.createElement("strong");

    value.textContent = status;

    row.append(
        label,
        value
    );

    container.appendChild(row);

    const deviceType = String(
        device.type ||
        device.deviceType ||
        "UNKNOWN"
    ).toUpperCase();

    /*
     * These device types can have a main physical
     * ON/OFF control.
     */
    const usesSimplePowerControl =
        deviceType === "OUTLET" ||
        deviceType === "LIGHT" ||
        deviceType === "IRON" ||
        deviceType === "SAFETY_DEVICE" ||
        deviceType === "SAFETY-DEVICE" ||
        deviceType === "MULTI_SWITCH" ||
        deviceType === "MULTI-SWITCH";

    if (usesSimplePowerControl) {
        const powerRow =
            document.createElement("div");

        powerRow.className =
            "control-row";

        const powerLabel =
            document.createElement("div");

        powerLabel.className =
            "control-label";

        const powerTitle =
            document.createElement("strong");

        const isMultiSwitch =
            deviceType === "MULTI_SWITCH" ||
            deviceType === "MULTI-SWITCH";

        powerTitle.textContent =
            isMultiSwitch
                ? "Multi-switch master power"
                : "Hardware power";

        const powerDescription =
            document.createElement("span");

        powerDescription.textContent =
            isMultiSwitch
                ? "Control power to the complete multi-switch"
                : "Simulate the physical ON/OFF switch";

        powerLabel.append(
            powerTitle,
            powerDescription
        );

        const toggleLabel =
            document.createElement("label");

        toggleLabel.className =
            "toggle";

        const toggleInput =
            document.createElement("input");

        toggleInput.type =
            "checkbox";

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

        container.appendChild(
            powerRow
        );
    }

    /*
     * Show the individual power outlets belonging
     * to a multi-switch.
     */
    if (
        deviceType === "MULTI_SWITCH" ||
        deviceType === "MULTI-SWITCH"
    ) {
        const multiSwitchHtml =
            renderMultiSwitchPoints(
                device.id,
                device
            );

        container.insertAdjacentHTML(
            "beforeend",
            multiSwitchHtml
        );
    }

    /*
     * Display maximum duration when available.
     */
    const maximumDuration =
        device.maxOnDuration ??
        device.maxOnDurationSeconds ??
        device.max_on_duration;

    if (maximumDuration !== undefined) {
        const durationRow =
            document.createElement("div");

        durationRow.className =
            "control-row";

        durationRow.textContent =
            `Maximum ON duration: ` +
            `${maximumDuration} minutes`;

        container.appendChild(
            durationRow
        );
    }

    /*
     * Display schedule information when available.
     */
    if (device.scheduleEnabled === true) {
        const scheduleRow =
            document.createElement("div");

        scheduleRow.className =
            "control-row";

        const onTime =
            device.onTime ||
            "Not set";

        const offTime =
            device.offTime ||
            "Not set";

        scheduleRow.textContent =
            `Schedule: ${onTime} - ${offTime}`;

        container.appendChild(
            scheduleRow
        );
    }
}


/*
 * Display devices and update summary cards.
 */
export function displayDevices(
    devices,
    floors
) {
    loadingState.classList.add(
        "hidden"
    );

    errorState.classList.add(
        "hidden"
    );

    deviceGrid.innerHTML = "";

    updateSummaryCards(devices);

    if (devices.length === 0) {
        emptyState.classList.remove(
            "hidden"
        );

        return;
    }

    emptyState.classList.add(
        "hidden"
    );

    devices.forEach((device) => {
        const cardFragment =
            deviceCardTemplate.content.cloneNode(
                true
            );

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

        card.dataset.deviceId =
            device.id;

        card.querySelector(
            ".device-name"
        ).textContent = name;

        card.querySelector(
            ".device-location"
        ).textContent =
            getFloorName(
                device,
                floors
            );

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
            card.querySelector(
                ".status-badge"
            );

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
 *
 * Multi-switch points are not counted as separate
 * devices.
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