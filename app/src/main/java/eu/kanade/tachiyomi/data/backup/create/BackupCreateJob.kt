package eu.kanade.tachiyomi.data.backup.create

import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.data.backup.BackupNotifier
import eu.kanade.tachiyomi.data.backup.restore.BackupRestoreJob
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.cancelNotification
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskBackoffCriteria
import tachiyomi.core.platform.background.BackgroundTaskBackoffPolicy
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskConstraints
import tachiyomi.core.platform.background.BackgroundTaskInputData
import tachiyomi.core.platform.background.BackgroundTaskInputValue
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.domain.backup.service.BackupPreferences
import tachiyomi.domain.storage.service.StorageManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class BackupCreateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val notifier = BackupNotifier(context)

    override suspend fun doWork(): Result {
        val isAutoBackup = inputData.getBoolean(IS_AUTO_BACKUP_KEY, true)

        if (isAutoBackup && BackupRestoreJob.isRunning(context)) return Result.retry()

        val uri = inputData.getString(LOCATION_URI_KEY)?.toUri()
            ?: getAutomaticBackupLocation()
            ?: return Result.failure()

        setForegroundSafely()

        val options = inputData.getBooleanArray(OPTIONS_KEY)?.let { BackupOptions.fromBooleanArray(it) }
            ?: BackupOptions()

        return try {
            val location = BackupCreator(context, isAutoBackup).backup(uri, options)
            if (!isAutoBackup) {
                notifier.showBackupComplete(UniFile.fromUri(context, location.toUri())!!)
            }
            Result.success()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            if (!isAutoBackup) notifier.showBackupError(e.message)
            Result.failure()
        } finally {
            context.cancelNotification(Notifications.ID_BACKUP_PROGRESS)
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            Notifications.ID_BACKUP_PROGRESS,
            notifier.showBackupProgress().build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    private fun getAutomaticBackupLocation(): Uri? {
        val storageManager = Injekt.get<StorageManager>()
        return storageManager.getAutomaticBackupsDirectory()?.uri
    }

    companion object {
        private const val WORKER_KEY = "backup_create"

        fun isManualJobRunning(
            context: Context,
            scheduler: BackgroundTaskScheduler = backupCreateScheduler(context),
        ): Boolean {
            return scheduler.isRunning(TAG_MANUAL)
        }

        fun setupTask(
            context: Context,
            prefInterval: Int? = null,
            scheduler: BackgroundTaskScheduler = backupCreateScheduler(context),
        ) {
            val backupPreferences = Injekt.get<BackupPreferences>()
            val interval = prefInterval ?: backupPreferences.backupInterval().get()
            if (interval > 0) {
                scheduler.schedule(
                    BackgroundTask(
                        uniqueName = TAG_AUTO,
                        workerKey = WORKER_KEY,
                        cadence = BackgroundTaskCadence.Periodic(
                            repeatInterval = interval.hours,
                            flexInterval = 10.minutes,
                        ),
                        constraints = BackgroundTaskConstraints(
                            requiresBatteryNotLow = true,
                        ),
                        policy = ExistingBackgroundTaskPolicy.Update,
                        inputData = BackgroundTaskInputData.of(
                            IS_AUTO_BACKUP_KEY to BackgroundTaskInputValue.BooleanValue(true),
                        ),
                        backoffCriteria = BackgroundTaskBackoffCriteria(
                            policy = BackgroundTaskBackoffPolicy.Exponential,
                            delay = 10.minutes,
                        ),
                        tags = setOf(TAG_AUTO),
                    ),
                )
            } else {
                scheduler.cancel(TAG_AUTO)
            }
        }

        fun startNow(
            context: Context,
            uri: Uri,
            options: BackupOptions,
            scheduler: BackgroundTaskScheduler = backupCreateScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG_MANUAL,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Keep,
                    inputData = BackgroundTaskInputData.of(
                        IS_AUTO_BACKUP_KEY to BackgroundTaskInputValue.BooleanValue(false),
                        LOCATION_URI_KEY to BackgroundTaskInputValue.StringValue(uri.toString()),
                        OPTIONS_KEY to BackgroundTaskInputValue.BooleanArrayValue(options.asBooleanArray()),
                    ),
                    tags = setOf(TAG_MANUAL),
                ),
            )
        }

        // KMK -->
        /**
         * Returns true if a periodic job is currently scheduled.
         * @param context The application context.
         * @return True if a periodic job is scheduled, false otherwise.
         * @throws Exception If there is an error retrieving the work info.
         */
        suspend fun isPeriodicBackupScheduled(
            context: Context,
            scheduler: BackgroundTaskScheduler = backupCreateScheduler(context),
        ): Boolean {
            return scheduler.isScheduled(TAG_AUTO)
        }
        // KMK <--

        private fun backupCreateScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> BackupCreateJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}

private const val TAG_AUTO = "BackupCreator"
private const val TAG_MANUAL = "$TAG_AUTO:manual"

private const val IS_AUTO_BACKUP_KEY = "is_auto_backup" // Boolean
private const val LOCATION_URI_KEY = "location_uri" // String
private const val OPTIONS_KEY = "options" // BooleanArray
