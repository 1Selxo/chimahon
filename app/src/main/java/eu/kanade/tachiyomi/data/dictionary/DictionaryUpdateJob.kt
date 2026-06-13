package eu.kanade.tachiyomi.data.dictionary

import android.app.Service
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import chimahon.HoshiDicts
import chimahon.dictionary.checkDictionaryUpdates
import chimahon.dictionary.readDictionaryIndex
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.ui.dictionary.DictionaryPreferences
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.core.platform.background.AndroidBackgroundWorkerRegistry
import tachiyomi.core.platform.background.AndroidWorkManagerBackgroundTaskScheduler
import tachiyomi.core.platform.background.BackgroundNetworkConstraint
import tachiyomi.core.platform.background.BackgroundTask
import tachiyomi.core.platform.background.BackgroundTaskCadence
import tachiyomi.core.platform.background.BackgroundTaskConstraints
import tachiyomi.core.platform.background.BackgroundTaskScheduler
import tachiyomi.core.platform.background.ExistingBackgroundTaskPolicy
import tachiyomi.i18n.MR
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

class DictionaryUpdateJob(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val notifier = DictionaryUpdateNotifier(context)
    private val prefs: DictionaryPreferences = Injekt.get()

    override suspend fun doWork(): Result {
        val isManual = tags.contains("manual")
        if (!isManual && !prefs.autoUpdateEnabled().get()) {
            Log.d(TAG, "Auto-update disabled, skipping periodic check")
            return Result.success()
        }

        val dictionariesDir = File(applicationContext.getExternalFilesDir(null), "dictionaries")
        if (!dictionariesDir.isDirectory) return Result.success()

        setForegroundSafely()
        notifier.showChecking()

        return try {
            val updates = withContext(Dispatchers.IO) {
                checkDictionaryUpdates(dictionariesDir)
                    .filter {
                        it.hasUpdate &&
                            (!it.downloadUrl.isNullOrBlank() || !it.latestDownloadUrl.isNullOrBlank())
                    }
            }

            prefs.lastDictUpdateCheck().set(System.currentTimeMillis())

            if (updates.isEmpty()) {
                Log.d(TAG, "No dictionary updates found")
                notifier.showNoUpdates()
                Result.success()
            } else {
                var successCount = 0
                var notifIndex = 0
                updates.forEachIndexed { index, update ->
                    notifier.showProgress(update.dictName, index, updates.size)
                    val dlUrl = update.latestDownloadUrl ?: update.downloadUrl
                    if (dlUrl != null) {
                        val result = applyUpdate(update.dictName, dlUrl, dictionariesDir)
                        if (result.success) {
                            successCount++
                            notifier.showUpdateResult(update.dictName, result.oldRevision, result.newRevision, notifIndex)
                            notifIndex++
                        }
                    }
                }

                notifier.showComplete(successCount)
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Dictionary update failed", e)
            notifier.hideChecking()
            notifier.showError("", e.message ?: "Unknown error")
            Result.failure()
        }
    }

    private data class UpdateResult(
        val success: Boolean,
        val oldRevision: String?,
        val newRevision: String?,
    )

    private suspend fun applyUpdate(
        dictName: String,
        downloadUrl: String,
        dictionariesDir: File,
    ): UpdateResult = withContext(Dispatchers.IO) {
        val cacheDir = applicationContext.cacheDir
        val tempZip = File(cacheDir, "dict_update_${System.currentTimeMillis()}.zip")
        val tempImportDir = File(cacheDir, "dict_import_tmp_${System.currentTimeMillis()}")

        try {
            Log.d(TAG, "Downloading $dictName from $downloadUrl")
            downloadFile(downloadUrl, tempZip)

            Log.d(TAG, "Importing $dictName to temp dir")
            tempImportDir.mkdirs()
            val hoshiResult = HoshiDicts.importDictionary(
                zipPath = tempZip.absolutePath,
                outputDir = tempImportDir.absolutePath,
            )

            if (!hoshiResult.success) {
                Log.e(TAG, "Import failed for $dictName")
                notifier.showError(dictName, "Import failed")
                return@withContext UpdateResult(false, null, null)
            }

            val importedSubdir = tempImportDir.listFiles()
                ?.firstOrNull { it.isDirectory }

            if (importedSubdir == null) {
                Log.e(TAG, "No imported directory found for $dictName")
                notifier.showError(dictName, "No imported directory found")
                return@withContext UpdateResult(false, null, null)
            }

            val types = buildList {
                if (hoshiResult.termCount > 0) add("term")
                if (hoshiResult.freqCount > 0) add("frequency")
                if (hoshiResult.pitchCount > 0) add("pitch")
            }

            // Read old revision before cleanup
            val oldRevision = readOldRevision(dictionariesDir, dictName)

            // Clean stale type subdirs from previous version before writing new ones
            for (type in listOf("term", "frequency", "pitch")) {
                val staleDir = File(File(dictionariesDir, type), dictName)
                if (staleDir.exists()) staleDir.deleteRecursively()
            }

            for (type in types) {
                val typeDir = File(dictionariesDir, type)
                typeDir.mkdirs()
                val targetDir = File(typeDir, dictName)
                importedSubdir.copyRecursively(targetDir)
            }

            // Read new revision from the just-written index.json
            val newRevision = readNewRevision(dictionariesDir, dictName)

            val resolvedName = if (hoshiResult.title.isNotBlank()) hoshiResult.title else dictName
            Log.d(TAG, "Successfully updated $resolvedName in $types (${oldRevision ?: "?"} → ${newRevision ?: "?"})")
            UpdateResult(true, oldRevision, newRevision)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update $dictName", e)
            notifier.showError(dictName, e.message ?: "Unknown error")
            UpdateResult(false, null, null)
        } finally {
            if (tempZip.exists()) tempZip.delete()
            if (tempImportDir.exists()) tempImportDir.deleteRecursively()
        }
    }

    private fun readOldRevision(dictionariesDir: File, dictName: String): String? {
        for (type in listOf("term", "frequency", "pitch")) {
            val dir = File(File(dictionariesDir, type), dictName)
            if (dir.isDirectory) {
                val index = readDictionaryIndex(dir)
                if (index?.revision != null) return index.revision
            }
        }
        return null
    }

    private fun readNewRevision(dictionariesDir: File, dictName: String): String? {
        for (type in listOf("term", "frequency", "pitch")) {
            val dir = File(File(dictionariesDir, type), dictName)
            if (dir.isDirectory) {
                val index = readDictionaryIndex(dir)
                if (index?.revision != null) return index.revision
            }
        }
        return null
    }

    private fun downloadFile(url: String, output: File) {
        val request = Request.Builder().url(url).build()
        val response = downloadClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Download failed: HTTP ${response.code}")
        }
        response.body?.byteStream()?.use { input ->
            output.outputStream().use { outputStream ->
                input.copyTo(outputStream)
            }
        }
    }

    override suspend fun getForegroundInfo(): androidx.work.ForegroundInfo {
        val notification = notifier.run {
            androidx.core.app.NotificationCompat.Builder(
                applicationContext,
                Notifications.CHANNEL_DICTIONARY_UPDATE,
            )
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(
                    applicationContext.stringResource(MR.strings.dict_update_progress_title),
                )
                .setOngoing(true)
                .build()
        }
        return androidx.work.ForegroundInfo(
            Notifications.ID_DICT_UPDATE_PROGRESS,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    companion object {
        private const val TAG = "DictUpdateJob"
        private const val UNIQUE_WORK_NAME = "DictionaryUpdate-auto"
        private const val MANUAL_WORK_NAME = "DictionaryUpdate-manual"
        private const val MANUAL_TAG = "manual"
        private const val WORKER_KEY = "dictionary_update"

        private val downloadClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()

        fun checkNow(
            context: Context,
            scheduler: BackgroundTaskScheduler = dictionaryUpdateScheduler(context),
        ) {
            scheduler.schedule(
                BackgroundTask(
                    uniqueName = MANUAL_WORK_NAME,
                    workerKey = WORKER_KEY,
                    cadence = BackgroundTaskCadence.OneTime,
                    constraints = BackgroundTaskConstraints(
                        network = BackgroundNetworkConstraint.Connected,
                    ),
                    policy = ExistingBackgroundTaskPolicy.Keep,
                    tags = setOf(TAG, MANUAL_TAG),
                ),
            )
            Log.d(TAG, "Manual dictionary update check enqueued")
        }

        fun setupTask(
            context: Context,
            enabled: Boolean,
            intervalHours: Int = 24,
            scheduler: BackgroundTaskScheduler = dictionaryUpdateScheduler(context),
        ) {
            if (enabled) {
                val interval = intervalHours.coerceIn(1, 168)
                val flex = (interval / 24).coerceIn(1, 6)
                scheduler.schedule(
                    BackgroundTask(
                        uniqueName = UNIQUE_WORK_NAME,
                        workerKey = WORKER_KEY,
                        cadence = BackgroundTaskCadence.Periodic(
                            repeatInterval = interval.hours,
                            flexInterval = flex.hours,
                        ),
                        constraints = BackgroundTaskConstraints(
                            network = BackgroundNetworkConstraint.Connected,
                        ),
                        policy = ExistingBackgroundTaskPolicy.Update,
                        tags = setOf(TAG),
                    ),
                )
                Log.d(TAG, "Scheduled daily dictionary update check")
            } else {
                scheduler.cancel(UNIQUE_WORK_NAME)
                Log.d(TAG, "Cancelled dictionary update check")
            }
        }

        private fun dictionaryUpdateScheduler(context: Context): BackgroundTaskScheduler {
            return AndroidWorkManagerBackgroundTaskScheduler(
                context = context,
                workerRegistry = AndroidBackgroundWorkerRegistry { workerKey ->
                    when (workerKey) {
                        WORKER_KEY -> DictionaryUpdateJob::class.java
                        else -> null
                    }
                },
            )
        }
    }
}
