package tachiyomi.core.platform.background

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import platform.posix.system
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class WindowsBackgroundTaskScheduler(
    workerRegistry: BackgroundWorkerRegistry,
    scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    constraintMonitor: BackgroundTaskConstraintMonitor = WindowsBackgroundTaskConstraintMonitor(),
) : BackgroundTaskScheduler by CoroutineBackgroundTaskScheduler(
    workerRegistry = workerRegistry,
    scope = scope,
    constraintMonitor = constraintMonitor,
)

class WindowsBackgroundTaskConstraintMonitor(
    private val pollInterval: Duration = 30.seconds,
    private val commandRunner: (String) -> Boolean = ::runWindowsCommand,
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
        const val NETWORK_CONNECTED =
            """powershell.exe -NoProfile -NonInteractive -Command "${'$'}p=Get-NetConnectionProfile -ErrorAction SilentlyContinue; if(${'$'}p ^| Where-Object {${'$'}_.IPv4Connectivity -eq 'Internet' -or ${'$'}_.IPv6Connectivity -eq 'Internet'}){exit 0}else{exit 1}""""
        const val WIFI_CONNECTED =
            """powershell.exe -NoProfile -NonInteractive -Command "if(Get-NetAdapter -Physical -ErrorAction SilentlyContinue ^| Where-Object {${'$'}_.Status -eq 'Up' -and ${'$'}_.NdisPhysicalMedium -eq 9}){exit 0}else{exit 1}""""
        const val UNMETERED_NETWORK =
            """powershell.exe -NoProfile -NonInteractive -Command "Add-Type -AssemblyName System.Runtime.WindowsRuntime; [Windows.Networking.Connectivity.NetworkInformation,Windows.Networking.Connectivity,ContentType=WindowsRuntime] ^| Out-Null; ${'$'}p=[Windows.Networking.Connectivity.NetworkInformation]::GetInternetConnectionProfile(); if(${'$'}p -and ${'$'}p.GetConnectionCost().NetworkCostType -in 'Unrestricted','Unknown'){exit 0}else{exit 1}""""
        const val CHARGING =
            """powershell.exe -NoProfile -NonInteractive -Command "${'$'}b=Get-CimInstance Win32_Battery -ErrorAction SilentlyContinue; if(!${'$'}b -or ${'$'}b.BatteryStatus -in 2,6,7,8,9){exit 0}else{exit 1}""""
        const val BATTERY_NOT_LOW =
            """powershell.exe -NoProfile -NonInteractive -Command "${'$'}b=Get-CimInstance Win32_Battery -ErrorAction SilentlyContinue; if(!${'$'}b -or ${'$'}b.EstimatedChargeRemaining -ge 15){exit 0}else{exit 1}""""
    }
}

private fun runWindowsCommand(command: String): Boolean = system(command) == 0
