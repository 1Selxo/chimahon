package eu.kanade.tachiyomi.data.backup.restore

import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.data.BackupRestoreStatus
import eu.kanade.tachiyomi.data.backup.BackupNotifier
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.cancelNotification
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.core.common.util.system.logcat
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskInputData
import tachiyomi.core.platform.background.BackgroundTaskInputValue
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.i18n.MR
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class BackupRestoreJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val notifier = BackupNotifier(context)

    // KMK -->
    private val backupRestoreStatus: BackupRestoreStatus = Injekt.get()
    // KMK <--

    override suspend fun doWork(): Result {
        val uri = inputData.getString(LOCATION_URI_KEY)?.toUri()
        val options = inputData.getBooleanArray(OPTIONS_KEY)?.let { RestoreOptions.fromBooleanArray(it) }

        if (uri == null || options == null) {
            return Result.failure()
        }

        // KMK -->
        backupRestoreStatus.start()
        // KMK <--

        val isSync = inputData.getBoolean(SYNC_KEY, false)

        setForegroundSafely()

        return try {
            BackupRestorer(context, notifier, isSync).restore(uri, options)
            Result.success()
        } catch (e: Exception) {
            if (e is CancellationException) {
                notifier.showRestoreError(context.stringResource(MR.strings.restoring_backup_canceled))
                Result.success()
            } else {
                logcat(LogPriority.ERROR, e)
                notifier.showRestoreError(e.message)
                Result.failure()
            }
        } finally {
            context.cancelNotification(Notifications.ID_RESTORE_PROGRESS)
            // KMK -->
            backupRestoreStatus.stop()
            // KMK <--
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            Notifications.ID_RESTORE_PROGRESS,
            notifier.showRestoreProgress().build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    companion object {
        private const val WORKER_KEY = "backup_restore"

        fun isRunning(
            context: Context,
            scheduler: BackgroundTaskScheduler = backupRestoreScheduler(context),
        ): Boolean {
            return scheduler.isRunning(TAG)
        }

        fun start(
            context: Context,
            uri: Uri,
            options: RestoreOptions,
            sync: Boolean = false,
            scheduler: BackgroundTaskScheduler = backupRestoreScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Keep,
                    inputData = BackgroundTaskInputData.of(
                        LOCATION_URI_KEY to BackgroundTaskInputValue.StringValue(uri.toString()),
                        SYNC_KEY to BackgroundTaskInputValue.BooleanValue(sync),
                        OPTIONS_KEY to BackgroundTaskInputValue.BooleanArrayValue(options.asBooleanArray()),
                    ),
                    tags = setOf(TAG),
                ),
            )
        }

        fun stop(
            context: Context,
            scheduler: BackgroundTaskScheduler = backupRestoreScheduler(context),
        ) {
            scheduler.cancel(TAG)
            // KMK -->
            val backupRestoreStatus: BackupRestoreStatus = Injekt.get()
            runBlocking { backupRestoreStatus.stop() }
            // KMK <--
        }

        private fun backupRestoreScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> BackupRestoreJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}

private const val TAG = "BackupRestore"

private const val LOCATION_URI_KEY = "location_uri" // String
private const val SYNC_KEY = "sync" // Boolean
private const val OPTIONS_KEY = "options" // BooleanArray
