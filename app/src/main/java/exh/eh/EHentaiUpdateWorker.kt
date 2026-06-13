package exh.eh

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.elvishew.xlog.Logger
import com.elvishew.xlog.XLog
import eu.kanade.domain.chapter.interactor.SyncChaptersWithSource
import eu.kanade.domain.manga.interactor.UpdateManga
import eu.kanade.domain.manga.model.toSManga
import eu.kanade.tachiyomi.data.library.LibraryUpdateNotifier
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.source.online.all.EHentai
import eu.kanade.tachiyomi.util.system.isConnectedToWifi
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import exh.debug.DebugToggles
import exh.eh.EHentaiUpdateWorkerConstants.UPDATES_PER_ITERATION
import exh.log.xLog
import exh.metadata.metadata.EHentaiSearchMetadata
import exh.source.ExhPreferences
import exh.util.cancellable
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import tachiyomi.core.common.preference.getAndSet
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundNetworkConstraint
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskConstraints
import tachiyomi.core.platform.background.BackgroundTaskOutOfQuotaPolicy
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.domain.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.library.service.LibraryPreferences.Companion.DEVICE_CHARGING
import tachiyomi.domain.library.service.LibraryPreferences.Companion.DEVICE_ONLY_ON_WIFI
import tachiyomi.domain.manga.interactor.GetExhFavoriteMangaWithMetadata
import tachiyomi.domain.manga.interactor.GetFlatMetadataById
import tachiyomi.domain.manga.interactor.InsertFlatMetadata
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.source.service.SourceManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class EHentaiUpdateWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    private val exhPreferences: ExhPreferences by injectLazy()
    private val libraryPreferences: LibraryPreferences by injectLazy()
    private val sourceManager: SourceManager by injectLazy()
    private val updateHelper: EHentaiUpdateHelper by injectLazy()
    private val logger: Logger by lazy { xLog() }
    private val updateManga: UpdateManga by injectLazy()
    private val syncChaptersWithSource: SyncChaptersWithSource by injectLazy()
    private val getChaptersByMangaId: GetChaptersByMangaId by injectLazy()
    private val getFlatMetadataById: GetFlatMetadataById by injectLazy()
    private val insertFlatMetadata: InsertFlatMetadata by injectLazy()
    private val getExhFavoriteMangaWithMetadata: GetExhFavoriteMangaWithMetadata by injectLazy()

    private val updateNotifier by lazy { EHentaiUpdateNotifier(context) }
    private val libraryUpdateNotifier by lazy { LibraryUpdateNotifier(context) }

    override suspend fun doWork(): Result {
        return try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P &&
                requiresWifiConnection(exhPreferences) &&
                !context.isConnectedToWifi()
            ) {
                logger.d("Retry again later in next periodic run due to missing Wi-Fi connection...")
                Result.success() // retry again later in next periodic run
            } else {
                setForegroundSafely()
                startUpdating()
                logger.d("Update job completed!")
                Result.success()
            }
        } catch (e: Exception) {
            logger.d("Retry again later in next periodic run!", e)
            Result.success() // retry again later in next periodic run
        } finally {
            updateNotifier.cancelProgressNotification()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            Notifications.ID_EHENTAI_PROGRESS,
            updateNotifier.progressNotificationBuilder.build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    private suspend fun startUpdating() {
        logger.d("Update job started!")
        val startTime = System.currentTimeMillis()

        logger.d("Finding manga with metadata...")
        val metadataManga = getExhFavoriteMangaWithMetadata.await()

        logger.d("Filtering manga and raising metadata...")
        val curTime = System.currentTimeMillis()
        val allMeta = metadataManga.asFlow().cancellable().mapNotNull { manga ->
            val meta = getFlatMetadataById.await(manga.id)
                ?: return@mapNotNull null

            val raisedMeta = meta.raise<EHentaiSearchMetadata>()

            // Don't update galleries too frequently
            if (raisedMeta.aged ||
                (
                    curTime - raisedMeta.lastUpdateCheck < MIN_BACKGROUND_UPDATE_FREQ &&
                        DebugToggles.RESTRICT_EXH_GALLERY_UPDATE_CHECK_FREQUENCY.enabled
                    )
            ) {
                return@mapNotNull null
            }

            val chapter = getChaptersByMangaId.await(manga.id).minByOrNull {
                it.dateUpload
            }

            UpdateEntry(manga, raisedMeta, chapter)
        }.toList().sortedBy { it.meta.lastUpdateCheck }

        logger.d("Found %s manga to update, starting updates!", allMeta.size)
        val mangaMetaToUpdateThisIter = allMeta.take(UPDATES_PER_ITERATION)

        var failuresThisIteration = 0
        var updatedThisIteration = 0
        val updatedManga = mutableListOf<Pair<Manga, Array<Chapter>>>()
        val modifiedThisIteration = mutableSetOf<Long>()

        try {
            for ((index, entry) in mangaMetaToUpdateThisIter.withIndex()) {
                val (manga, meta) = entry
                if (failuresThisIteration > MAX_UPDATE_FAILURES) {
                    logger.w("Too many update failures, aborting...")
                    break
                }

                logger.d(
                    "Updating gallery (index: %s, manga.id: %s, meta.gId: %s, meta.gToken: %s, failures-so-far: %s, modifiedThisIteration.size: %s)...",
                    index,
                    manga.id,
                    meta.gId,
                    meta.gToken,
                    failuresThisIteration,
                    modifiedThisIteration.size,
                )

                if (manga.id in modifiedThisIteration) {
                    // We already processed this manga!
                    logger.w("Gallery already updated this iteration, skipping...")
                    updatedThisIteration++
                    continue
                }

                val (new, chapters) = try {
                    updateNotifier.showProgressNotification(
                        manga,
                        updatedThisIteration + failuresThisIteration,
                        mangaMetaToUpdateThisIter.size,
                    )
                    updateEntryAndGetChapters(manga)
                } catch (e: GalleryNotUpdatedException) {
                    if (e.network) {
                        failuresThisIteration++

                        logger.e("> Network error while updating gallery!", e)
                        logger.e(
                            "> (manga.id: %s, meta.gId: %s, meta.gToken: %s, failures-so-far: %s)",
                            manga.id,
                            meta.gId,
                            meta.gToken,
                            failuresThisIteration,
                        )
                    }

                    continue
                }

                if (chapters.isEmpty()) {
                    logger.e(
                        "No chapters found for gallery (manga.id: %s, meta.gId: %s, meta.gToken: %s, failures-so-far: %s)!",
                        manga.id,
                        meta.gId,
                        meta.gToken,
                        failuresThisIteration,
                    )

                    continue
                }

                // Find accepted root and discard others
                val (acceptedRoot, discardedRoots, exhNew) =
                    updateHelper.findAcceptedRootAndDiscardOthers(manga.source, chapters)

                if (new.isNotEmpty() && manga.id == acceptedRoot.manga.id) {
                    libraryPreferences.newUpdatesCount().getAndSet { it + new.size }
                    updatedManga += acceptedRoot.manga to new.toTypedArray()
                } else if (exhNew.isNotEmpty() && updatedManga.none { it.first.id == acceptedRoot.manga.id }) {
                    libraryPreferences.newUpdatesCount().getAndSet { it + exhNew.size }
                    updatedManga += acceptedRoot.manga to exhNew.toTypedArray()
                }

                modifiedThisIteration += acceptedRoot.manga.id
                modifiedThisIteration += discardedRoots.map { it.manga.id }
                updatedThisIteration++
            }
        } finally {
            exhPreferences.exhAutoUpdateStats().set(
                Json.encodeToString(
                    EHentaiUpdaterStats(
                        startTime,
                        allMeta.size,
                        updatedThisIteration,
                    ),
                ),
            )

            updateNotifier.cancelProgressNotification()
            if (updatedManga.isNotEmpty()) {
                libraryUpdateNotifier.showUpdateNotifications(updatedManga)
            }
        }
    }

    // New, current
    private suspend fun updateEntryAndGetChapters(manga: Manga): Pair<List<Chapter>, List<Chapter>> {
        val source = sourceManager.get(manga.source) as? EHentai
            ?: throw GalleryNotUpdatedException(false, IllegalStateException("Missing EH-based source (${manga.source})!"))

        try {
            val updatedManga = source.getMangaDetails(manga.toSManga())
            updateManga.awaitUpdateFromSource(manga, updatedManga, false)

            val newChapters = source.getChapterList(manga.toSManga())

            val new = syncChaptersWithSource.await(newChapters, manga, source)
            return new to getChaptersByMangaId.await(manga.id)
        } catch (t: Throwable) {
            if (t is EHentai.GalleryNotFoundException) {
                val meta = getFlatMetadataById.await(manga.id)?.raise<EHentaiSearchMetadata>()
                if (meta != null) {
                    // Age dead galleries
                    logger.d("Aged %s - notfound", manga.id)
                    meta.aged = true
                    insertFlatMetadata.await(meta)
                }
                throw GalleryNotUpdatedException(false, t)
            }
            throw GalleryNotUpdatedException(true, t)
        }
    }

    companion object {
        private const val MAX_UPDATE_FAILURES = 5

        private val MIN_BACKGROUND_UPDATE_FREQ = 1.days.inWholeMilliseconds

        private const val TAG = "EHBackgroundUpdater"
        private const val TEST_WORK_NAME = "$TAG:test"
        private const val WORKER_KEY = "ehentai_update"

        private val logger by lazy { XLog.tag("EHUpdaterScheduler") }

        fun launchBackgroundTest(
            context: Context,
            scheduler: BackgroundTaskScheduler = ehentaiUpdateScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = TEST_WORK_NAME,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    policy = ExistingBackgroundTaskPolicy.Replace,
                    expeditedPolicy = BackgroundTaskOutOfQuotaPolicy.RunAsNonExpedited,
                    tags = setOf(TAG),
                ),
            )
        }

        fun scheduleBackground(
            context: Context,
            prefInterval: Int? = null,
            prefRestrictions: Set<String>? = null,
            scheduler: BackgroundTaskScheduler = ehentaiUpdateScheduler(context),
        ) {
            val exhPreferences = Injekt.get<ExhPreferences>()
            val interval = prefInterval ?: exhPreferences.exhAutoUpdateFrequency().get()
            if (interval > 0) {
                val restrictions = prefRestrictions ?: exhPreferences.exhAutoUpdateRequirements().get()
                val acRestriction = DEVICE_CHARGING in restrictions

                scheduler.schedule(
                    BackgroundTask(
                        uniqueName = TAG,
                        workerKey = WORKER_KEY,
                        cadence = BackgroundTaskCadence.Periodic(
                            repeatInterval = interval.hours,
                            flexInterval = 10.minutes,
                        ),
                        constraints = BackgroundTaskConstraints(
                            network = BackgroundNetworkConstraint.Connected,
                            requiresWifi = DEVICE_ONLY_ON_WIFI in restrictions,
                            requiresCharging = acRestriction,
                        ),
                        policy = ExistingBackgroundTaskPolicy.Update,
                        tags = setOf(TAG),
                    ),
                )
                logger.d("Successfully scheduled background update job!")
            } else {
                cancelBackground(context, scheduler)
            }
        }

        fun cancelBackground(
            context: Context,
            scheduler: BackgroundTaskScheduler = ehentaiUpdateScheduler(context),
        ) {
            scheduler.cancel(TAG)
        }

        private fun ehentaiUpdateScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> EHentaiUpdateWorker::class.java
                        else -> null
                    }
                },
            )
        }
    }

    private fun requiresWifiConnection(exhPreferences: ExhPreferences): Boolean {
        val restrictions = exhPreferences.exhAutoUpdateRequirements().get()
        return DEVICE_ONLY_ON_WIFI in restrictions
    }
}

data class UpdateEntry(val manga: Manga, val meta: EHentaiSearchMetadata, val rootChapter: Chapter?)

object EHentaiUpdateWorkerConstants {
    const val UPDATES_PER_ITERATION = 50

    val GALLERY_AGE_TIME = 365.days.inWholeMilliseconds
}
