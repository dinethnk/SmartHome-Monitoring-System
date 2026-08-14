import {initializeApp} from "firebase-admin/app";
import {getDatabase} from "firebase-admin/database";
import {DateTime} from "luxon";

initializeApp({
  databaseURL:
    "https://smarthomemonitoringsyste-59316-default-rtdb.asia-southeast1.firebasedatabase.app/",
});

const db = getDatabase();

const TIME_ZONE = "Asia/Colombo";
const CHECK_INTERVAL_MS = 60 * 1000;

interface DeviceSchedule {
  deviceId: string;
  enabled: boolean;
  onTime: string;
  offTime: string;
}

interface Device {
  id?: string;
  name?: string;
  floorId?: string;
  type?: string;
  status?: string;
  row?: number;
  column?: number;
}


/**
 * Logs a message with the current Sri Lankan time.
 *
 * @param {string} message - Message to log.
 */
function log(message: string): void {
  const currentTime =
    DateTime.now()
      .setZone(TIME_ZONE)
      .toFormat("yyyy-MM-dd HH:mm:ss");

  console.log(
    `[${currentTime}] ${message}`
  );
}


/**
 * Converts a schedule time string into a DateTime.
 *
 * Supports both:
 * - 24-hour format: "18:00"
 * - 12-hour format: "06:00 PM"
 *
 * @param {string} time - Schedule time.
 * @return {DateTime|null} Parsed time or null when invalid.
 */
    function parseScheduleTime(
      time: string
    ): DateTime | null {
      const trimmedTime = time.trim();

      // -------------------------------------------------------
      // Try 24-hour format first.
      // Example: 18:00
      // -------------------------------------------------------

      let parsed =
        DateTime.fromFormat(
          trimmedTime,
          "HH:mm",
          {
            zone: TIME_ZONE,
          }
        );

      if (parsed.isValid) {
        return parsed;
      }

      // -------------------------------------------------------
      // Try 12-hour format.
      // Example: 06:00 PM
      // -------------------------------------------------------

      parsed =
        DateTime.fromFormat(
          trimmedTime,
          "hh:mm a",
          {
            zone: TIME_ZONE,
          }
        );

      if (parsed.isValid) {
        return parsed;
      }

      console.error(
        `Invalid schedule time: ${time}`
      );

      return null;
    }


/**
 * Determines whether a device should currently be ON.
 *
 * @param {DateTime} currentTime - Current local time.
 * @param {DateTime} onTime - Scheduled ON time.
 * @param {DateTime} offTime - Scheduled OFF time.
 * @return {boolean} True when the device should be ON.
 */
function shouldDeviceBeOn(
  currentTime: DateTime,
  onTime: DateTime,
  offTime: DateTime
): boolean {
  const currentMinutes =
    currentTime.hour * 60 +
    currentTime.minute;

  const onMinutes =
    onTime.hour * 60 +
    onTime.minute;

  const offMinutes =
    offTime.hour * 60 +
    offTime.minute;

  // Same ON and OFF time means
  // there is no active schedule period.
  if (onMinutes === offMinutes) {
    return false;
  }

  // Normal schedule.
  //
  // Example:
  // 06:00 PM -> 10:00 PM
  if (onMinutes < offMinutes) {
    return (
      currentMinutes >= onMinutes &&
      currentMinutes < offMinutes
    );
  }

  // Overnight schedule.
  //
  // Example:
  // 06:00 PM -> 06:00 AM
  return (
    currentMinutes >= onMinutes ||
    currentMinutes < offMinutes
  );
}


/**
 * Processes all enabled light schedules.
 */
async function processLightSchedules(): Promise<void> {
  log(
    "========================================"
  );

  log(
    "Light schedule worker started"
  );

  log(
    "========================================"
  );

  // -------------------------------------------------------
  // Current Sri Lankan time
  // -------------------------------------------------------

  const currentTime =
    DateTime.now()
      .setZone(TIME_ZONE);

  log(
    `Current time: ${
      currentTime.toFormat("hh:mm a")
    }`
  );


  // -------------------------------------------------------
  // Read all schedules
  // -------------------------------------------------------

  const schedulesSnapshot =
    await db
      .ref("schedules")
      .once("value");


  if (!schedulesSnapshot.exists()) {
    log(
      "No schedules found."
    );

    return;
  }


  const schedules =
    schedulesSnapshot.val() as
      Record<string, DeviceSchedule>;


  // -------------------------------------------------------
  // Process every schedule
  // -------------------------------------------------------

  for (
    const deviceId of Object.keys(schedules)
  ) {
    const schedule =
      schedules[deviceId];

    log(
      `Processing schedule for device: ${deviceId}`
    );


    // -----------------------------------------------------
    // Check whether schedule is enabled
    // -----------------------------------------------------

    if (!schedule.enabled) {
      log(
        `Schedule disabled for device: ${deviceId}`
      );

      continue;
    }


    // -----------------------------------------------------
    // Validate schedule
    // -----------------------------------------------------

    if (
      !schedule.onTime ||
      !schedule.offTime
    ) {
      log(
        `Incomplete schedule for device: ${deviceId}`
      );

      continue;
    }


    // -----------------------------------------------------
    // Parse ON and OFF times
    // -----------------------------------------------------

    const onTime =
      parseScheduleTime(
        schedule.onTime
      );

    const offTime =
      parseScheduleTime(
        schedule.offTime
      );


    if (
      !onTime ||
      !offTime
    ) {
      log(
        `Unable to parse schedule for device: ${deviceId}`
      );

      continue;
    }


    // -----------------------------------------------------
    // Determine required state
    // -----------------------------------------------------

    const shouldBeOn =
      shouldDeviceBeOn(
        currentTime,
        onTime,
        offTime
      );

    const desiredStatus =
      shouldBeOn
        ? "ON"
        : "OFF";


    // -----------------------------------------------------
    // Read device
    // -----------------------------------------------------

    const deviceReference =
      db.ref(
        `devices/${deviceId}`
      );

    const deviceSnapshot =
      await deviceReference.once(
        "value"
      );


    if (!deviceSnapshot.exists()) {
      log(
        `Device not found: ${deviceId}`
      );

      continue;
    }


    const device =
      deviceSnapshot.val() as Device;

    const currentStatus =
      device.status;


    // -----------------------------------------------------
    // Log current situation
    // -----------------------------------------------------

    log(
      `Device: ${
        device.name ?? deviceId
      } | ` +
      `Schedule: ${
        schedule.onTime
      } -> ${
        schedule.offTime
      } | ` +
      `Current: ${
        currentStatus
      } | ` +
      `Required: ${
        desiredStatus
      }`
    );


    // -----------------------------------------------------
    // No update required
    // -----------------------------------------------------

    if (
      currentStatus === desiredStatus
    ) {
      log(
        `No state change required for ${
          device.name ?? deviceId
        }`
      );

      continue;
    }


    // -----------------------------------------------------
    // Update Firebase device status
    // -----------------------------------------------------

    await deviceReference.update({
      status: desiredStatus,
    });


    log(
      `Device ${
        device.name ?? deviceId
      } changed: ${
        currentStatus
      } -> ${
        desiredStatus
      }`
    );
  }


  log(
    "========================================"
  );

  log(
    "Light schedule worker completed"
  );

  log(
    "========================================"
  );
}


/**
 * Waits for the specified amount of time.
 *
 * @param {number} milliseconds - Amount of time to wait.
 * @return {Promise<void>} Promise completed after the delay.
 */
function sleep(
  milliseconds: number
): Promise<void> {
  return new Promise(
    (resolve) => {
      setTimeout(
        resolve,
        milliseconds
      );
    }
  );
}


/**
 * Starts the continuous scheduler worker.
 */
async function startWorker(): Promise<void> {
  log(
    "========================================"
  );

  log(
    "Smart Home Scheduler Worker"
  );

  log(
    "Worker started successfully."
  );

  log(
    `Timezone: ${TIME_ZONE}`
  );

  log(
    "Checking schedules every 60 seconds."
  );

  log(
    "========================================"
  );


  while (true) {
    try {
      await processLightSchedules();
    } catch (error) {
      console.error(
        "Scheduler worker error:",
        error
      );
    }

    await sleep(
      CHECK_INTERVAL_MS
    );
  }
}


startWorker().catch(
  (error) => {
    console.error(
      "Worker stopped unexpectedly:",
      error
    );

    process.exit(1);
  }
);