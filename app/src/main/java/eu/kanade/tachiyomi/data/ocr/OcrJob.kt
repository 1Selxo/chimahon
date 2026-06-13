package eu.kanade.tachiyomi.data.ocr

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.notificationBuilder
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import kotlinx.coroutines.CancellationException
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

class OcrJob(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val ocrManager: OcrManager = Injekt.get()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = applicationContext.notificationBuilder(Notifications.CHANNEL_DOWNLOADER_PROGRESS) {
            setContentTitle(applicationContext.getString(R.string.ocr_running))
            setSmallIcon(android.R.drawable.stat_sys_download_done)
        }.build()
        return ForegroundInfo(
            Notifications.ID_OCR_PROGRESS,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    override suspend fun doWork(): Result {
        // Show foreground notification immediately to keep worker alive
        setForegroundSafely()

        return try {
            ocrManager.runPendingQueue(stopRequested = { isStopped })
            Result.success()
        } catch (e: CancellationException) {
            logcat(LogPriority.WARN, e) { "OcrJob cancelled while processing OCR queue" }
            throw e
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e) { "OcrJob failed" }
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "OcrJob"
        private const val WORKER_KEY = "ocr"

        fun start(
            context: Context,
            scheduler: BackgroundTaskScheduler = ocrScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Keep,
                    tags = setOf(TAG),
                ),
            )
        }

        fun stop(
            context: Context,
            scheduler: BackgroundTaskScheduler = ocrScheduler(context),
        ) {
            scheduler.cancel(TAG)
        }

        fun isRunning(
            context: Context,
            scheduler: BackgroundTaskScheduler = ocrScheduler(context),
        ): Boolean {
            return scheduler.isRunning(TAG)
        }

        private fun ocrScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> OcrJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
