package tachiyomi.core.platform.background

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowsBackgroundTaskConstraintMonitorTest {

    @Test
    fun unconstrainedTaskDoesNotProbeOperatingSystem() {
        var commands = 0
        val monitor = WindowsBackgroundTaskConstraintMonitor {
            commands++
            false
        }

        assertTrue(monitor.constraintsAreMet(BackgroundTaskConstraints()))
        assertEquals(0, commands)
    }

    @Test
    fun unavailableNetworkBlocksConstrainedTask() {
        var commands = 0
        val monitor = WindowsBackgroundTaskConstraintMonitor {
            commands++
            false
        }

        val result = monitor.constraintsAreMet(
            BackgroundTaskConstraints(network = BackgroundNetworkConstraint.Connected),
        )

        assertFalse(result)
        assertEquals(1, commands)
    }

    @Test
    fun everyRequestedConstraintMustPass() {
        var commands = 0
        val monitor = WindowsBackgroundTaskConstraintMonitor {
            commands++
            true
        }

        val result = monitor.constraintsAreMet(
            BackgroundTaskConstraints(
                network = BackgroundNetworkConstraint.Unmetered,
                requiresWifi = true,
                requiresCharging = true,
                requiresBatteryNotLow = true,
            ),
        )

        assertTrue(result)
        assertEquals(5, commands)
    }
}
