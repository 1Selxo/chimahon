package eu.kanade.tachiyomi.data.sync

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import eu.kanade.domain.sync.SyncPreferences
import eu.kanade.tachiyomi.data.SyncStatus
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.isOnline
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.minutes

class SyncDataJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val notifier = SyncNotifier(context)

    // KMK -->
    private val syncStatus: SyncStatus = Injekt.get()
    // KMK <--

    override suspend fun doWork(): Result {
        if (tags.contains(TAG_AUTO)) {
            if (!context.isOnline()) {
                return Result.retry()
            }
            // Find a running manual worker. If exists, try again later
            if (syncScheduler(context).isRunningWithTag(TAG_MANUAL)) {
                return Result.retry()
            }
        }

        // KMK -->
        syncStatus.start()
        // KMK <--

        setForegroundSafely()

        return try {
            SyncManager(context).syncData()
            Result.success()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            notifier.showSyncError(e.message)
            Result.success() // try again next time
        } finally {
            // KMK -->
            syncStatus.stop()
            // KMK <--
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            Notifications.ID_SYNC_PROGRESS,
            notifier.showSyncProgress().build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    companion object {
        private const val TAG_JOB = "SyncDataJob"
        private const val TAG_AUTO = "$TAG_JOB:auto"
        const val TAG_MANUAL = "$TAG_JOB:manual"
        internal const val WORKER_KEY = "sync_data"

        fun isRunning(
            context: Context,
            scheduler: BackgroundTaskScheduler = syncScheduler(context),
        ): Boolean {
            return scheduler.isRunningWithTag(TAG_JOB)
        }

        fun setupTask(
            context: Context,
            prefInterval: Int? = null,
            scheduler: BackgroundTaskScheduler = syncScheduler(context),
        ) {
            val syncPreferences = Injekt.get<SyncPreferences>()
            val interval = prefInterval ?: syncPreferences.syncInterval().get()

            if (interval > 0) {
                scheduler.schedule(
                    BackgroundTask(
                        uniqueName = TAG_AUTO,
                        workerKey = WORKER_KEY,
                        cadence = BackgroundTaskCadence.Periodic(
                            repeatInterval = interval.minutes,
                            flexInterval = 10.minutes,
                        ),
                        policy = ExistingBackgroundTaskPolicy.Update,
                        tags = setOf(TAG_JOB, TAG_AUTO),
                    ),
                )
            } else {
                scheduler.cancel(TAG_AUTO)
            }
        }

        fun startNow(
            context: Context,
            manual: Boolean = false,
            scheduler: BackgroundTaskScheduler = syncScheduler(context),
        ) {
            if (scheduler.isRunningWithTag(TAG_JOB)) {
                // Already running either as a scheduled or manual job
                return
            }
            val tag = if (manual) TAG_MANUAL else TAG_AUTO
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = tag,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Keep,
                    tags = setOf(TAG_JOB, tag),
                ),
            )
        }

        fun stop(
            context: Context,
            scheduler: BackgroundTaskScheduler = syncScheduler(context),
        ) {
            // KMK -->
            val syncPreferences = Injekt.get<SyncPreferences>()
            val syncEnabled = syncPreferences.isSyncEnabled()
            // KMK <--
            scheduler.runningTasksWithTag(TAG_JOB)
                // Should only return one work but just in case
                .forEach {
                    scheduler.cancelTask(it)
                    // KMK -->
                    val syncStatus: SyncStatus = Injekt.get()
                    runBlocking { syncStatus.stop() }
                    // KMK <--

                    // Re-enqueue cancelled scheduled work
                    if (/* KMK --> */ syncEnabled /* KMK <-- */ && it.tags.contains(TAG_AUTO)) {
                        setupTask(context, scheduler = scheduler)
                    }
                }
        }

        private fun syncScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> SyncDataJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
