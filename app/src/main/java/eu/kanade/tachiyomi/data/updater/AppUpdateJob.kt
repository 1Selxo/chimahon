package eu.kanade.tachiyomi.data.updater

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.notificationManager
import eu.kanade.tachiyomi.util.system.updaterEnabled
import exh.log.xLogE
import kotlinx.coroutines.coroutineScope
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundNetworkConstraint
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskConstraints
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class AppUpdateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            if (!updaterEnabled) {
                cancelTask(context)
                return@coroutineScope Result.success()
            }
            AppUpdateChecker().checkForUpdate(context)
            Result.success()
        } catch (e: Exception) {
            xLogE("Unable to check for update", e)
            Result.failure()
        }
    }

    fun NotificationCompat.Builder.update(block: NotificationCompat.Builder.() -> Unit) {
        block()
        context.notificationManager.notify(Notifications.ID_APP_UPDATER, build())
    }

    companion object {
        private const val TAG = "AppUpdateChecker"
        private const val WORKER_KEY = "app_update_check"

        fun setupTask(
            context: Context,
            scheduler: BackgroundTaskScheduler = appUpdateScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.Periodic(
                        repeatInterval = 3.days,
                        flexInterval = 3.hours,
                    ),
                    constraints = BackgroundTaskConstraints(
                        network = BackgroundNetworkConstraint.Connected,
                    ),
                    policy = ExistingBackgroundTaskPolicy.Update,
                    tags = setOf(TAG),
                ),
            )
        }

        fun cancelTask(
            context: Context,
            scheduler: BackgroundTaskScheduler = appUpdateScheduler(context),
        ) {
            // cancel and remove job
            scheduler.cancel(TAG)
            scheduler.prune()
        }

        private fun appUpdateScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> AppUpdateJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
