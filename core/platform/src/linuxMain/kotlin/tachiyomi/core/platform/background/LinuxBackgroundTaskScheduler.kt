package tachiyomi.core.platform.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import platform.posix.system
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class LinuxBackgroundTaskScheduler(
    workerRegistry: BackgroundWorkerRegistry,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    constraintMonitor: BackgroundTaskConstraintMonitor = LinuxBackgroundTaskConstraintMonitor(),
) : BackgroundTaskScheduler by CoroutineBackgroundTaskScheduler(
    workerRegistry = workerRegistry,
    scope = scope,
    constraintMonitor = constraintMonitor,
)

class LinuxBackgroundTaskConstraintMonitor(
    private val pollInterval: Duration = 30.seconds,
    private val commandRunner: (String) -> Boolean = ::runLinuxCommand,
) : BackgroundTaskConstraintMonitor {

    override suspend fun awaitConstraints(constraints: BackgroundTaskConstraints) {
        while (!constraintsAreMet(constraints)) {
            delay(pollInterval)
        }
    }

    internal fun constraintsAreMet(constraints: BackgroundTaskConstraints): Boolean {
        if (constraints.network != BackgroundNetworkConstraint.NotRequired && !commandRunner(NETWORK_CONNECTED)) {
            return false
        }
        if (constraints.requiresWifi && !commandRunner(WIFI_CONNECTED)) {
            return false
        }
        if (constraints.network == BackgroundNetworkConstraint.Unmetered && !commandRunner(UNMETERED_NETWORK)) {
            return false
        }
        if (constraints.requiresCharging && !commandRunner(CHARGING)) {
            return false
        }
        if (constraints.requiresBatteryNotLow && !commandRunner(BATTERY_NOT_LOW)) {
            return false
        }
        return true
    }

    private companion object {
        const val NETWORK_CONNECTED = "ip route get 1.1.1.1 >/dev/null 2>&1"
        const val WIFI_CONNECTED = "iwgetid -r >/dev/null 2>&1"
        const val UNMETERED_NETWORK =
            "nmcli -t -f GENERAL.METERED device show 2>/dev/null | grep -Eq ':(no|unknown)$'"
        const val CHARGING =
            "set -- /sys/class/power_supply/BAT*/status; [ ! -e \"${'$'}1\" ] || grep -Eq 'Charging|Full' \"${'$'}@\""
        const val BATTERY_NOT_LOW =
            "set -- /sys/class/power_supply/BAT*/capacity; [ ! -e \"${'$'}1\" ] || awk '{if (${'$'}1 < 15) exit 1}' \"${'$'}@\""
    }
}

private fun runLinuxCommand(command: String): Boolean = system(command) == 0
