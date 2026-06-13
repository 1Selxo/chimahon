package eu.kanade.domain.track.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.kanade.domain.track.interactor.TrackChapter
import eu.kanade.domain.track.store.DelayedTrackingStore
import logcat.LogPriority
import tachiyomi.core.common.util.lang.withIOContext
import tachiyomi.core.common.util.system.logcat
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundNetworkConstraint
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskBackoffCriteria
import tachiyomi.core.platform.background.BackgroundTaskBackoffPolicy
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskConstraints
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.domain.track.interactor.GetTracks
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlin.time.Duration.Companion.minutes

class DelayedTrackingUpdateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > 3) {
            return Result.failure()
        }

        val getTracks = Injekt.get<GetTracks>()
        val trackChapter = Injekt.get<TrackChapter>()

        val delayedTrackingStore = Injekt.get<DelayedTrackingStore>()

        withIOContext {
            delayedTrackingStore.getItems()
                .mapNotNull {
                    val track = getTracks.awaitOne(it.trackId)
                    if (track == null) {
                        delayedTrackingStore.remove(it.trackId)
                    }
                    track?.copy(lastChapterRead = it.lastChapterRead.toDouble())
                }
                .forEach { track ->
                    logcat(LogPriority.DEBUG) {
                        "Updating delayed track item: ${track.mangaId}, last chapter read: ${track.lastChapterRead}"
                    }
                    trackChapter.await(context, track.mangaId, track.lastChapterRead, setupJobOnFailure = false)
                }
        }

        return if (delayedTrackingStore.getItems().isEmpty()) Result.success() else Result.retry()
    }

    companion object {
        private const val TAG = "DelayedTrackingUpdate"
        private const val WORKER_KEY = "delayed_tracking_update"

        fun setupTask(
            context: Context,
            scheduler: BackgroundTaskScheduler = delayedTrackingScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TAG,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    constraints = BackgroundTaskConstraints(
                        network = BackgroundNetworkConstraint.Connected,
                    ),
                    policy = ExistingBackgroundTaskPolicy.Replace,
                    backoffCriteria = BackgroundTaskBackoffCriteria(
                        policy = BackgroundTaskBackoffPolicy.Exponential,
                        delay = 5.minutes,
                    ),
                    tags = setOf(TAG),
                ),
            )
        }

        private fun delayedTrackingScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> DelayedTrackingUpdateJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
