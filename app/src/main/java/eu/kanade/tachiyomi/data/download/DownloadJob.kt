package eu.kanade.tachiyomi.data.download

import android.content.Context
import android.content.pm.ServiceInfo
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.NetworkState
import eu.kanade.tachiyomi.util.system.activeNetworkState
import eu.kanade.tachiyomi.util.system.networkStateFlow
import eu.kanade.tachiyomi.util.system.notificationBuilder
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.domain.download.service.DownloadPreferences
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * This worker is used to manage the downloader. The system can decide to stop the worker, in
 * which case the downloader is also stopped. It's also stopped while there's no network available.
 */
class DownloadJob(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val downloadManager: DownloadManager = Injekt.get()
    private val downloadPreferences: DownloadPreferences = Injekt.get()

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = applicationContext.notificationBuilder(Notifications.CHANNEL_DOWNLOADER_PROGRESS) {
            setContentTitle(applicationContext.getString(R.string.download_notifier_downloader_title))
            setSmallIcon(android.R.drawable.stat_sys_download)
            setColor(ContextCompat.getColor(applicationContext, R.color.ic_launcher))
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.chimahon))
        }.build()
        return ForegroundInfo(
            Notifications.ID_DOWNLOAD_CHAPTER_PROGRESS,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    override suspend fun doWork(): Result {
        var networkCheck = checkNetworkState(
            applicationContext.activeNetworkState(),
            downloadPreferences.downloadOnlyOverWifi().get(),
        )
        var active = networkCheck && downloadManager.downloaderStart()

        if (!active) {
            return Result.failure()
        }

        setForegroundSafely()

        coroutineScope {
            combineTransform(
                applicationContext.networkStateFlow(),
                downloadPreferences.downloadOnlyOverWifi().changes(),
                transform = { a, b -> emit(checkNetworkState(a, b)) },
            )
                .onEach { networkCheck = it }
                .launchIn(this)
        }

        // Keep the worker running when needed
        while (active) {
            active = !isStopped && downloadManager.isRunning && networkCheck
        }

        return Result.success()
    }

    private fun checkNetworkState(state: NetworkState, requireWifi: Boolean): Boolean {
        return if (state.isOnline) {
            val noWifi = requireWifi && !state.isWifi
            if (noWifi) {
                downloadManager.downloaderStop(
                    applicationContext.getString(R.string.download_notifier_text_only_wifi),
                )
            }
            !noWifi
        } else {
            downloadManager.downloaderStop(applicationContext.getString(R.string.download_notifier_no_network))
            false
        }
    }

    companion object {
        private const val TAG = "Downloader"
        private const val WORKER_KEY = "download"

        fun start(
            context: Context,
            scheduler: BackgroundTaskScheduler = downloadScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Replace,
                    tags = setOf(TAG),
                ),
            )
        }

        fun stop(
            context: Context,
            scheduler: BackgroundTaskScheduler = downloadScheduler(context),
        ) {
            scheduler.cancel(TAG)
        }

        fun isRunning(
            context: Context,
            scheduler: BackgroundTaskScheduler = downloadScheduler(context),
        ): Boolean {
            return scheduler.isRunning(TAG)
        }

        fun isRunningFlow(
            context: Context,
            scheduler: BackgroundTaskScheduler = downloadScheduler(context),
        ): Flow<Boolean> {
            return scheduler.isRunningFlow(TAG)
        }

        private fun downloadScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> DownloadJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
