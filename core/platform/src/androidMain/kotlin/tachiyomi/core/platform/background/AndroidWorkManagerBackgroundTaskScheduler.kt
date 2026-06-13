package tachiyomi.core.platform.background

import android.content.Context
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.Observer
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

fun interface AndroidBackgroundWorkerRegistry {
    fun workerClass(workerKey: String): Class<out ListenableWorker>?
}

class AndroidWorkManagerBackgroundTaskScheduler(
    context: Context,
    private val workerRegistry: AndroidBackgroundWorkerRegistry,
) : BackgroundTaskScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun schedule(task: BackgroundTask) {
        val workerClass = workerRegistry.workerClass(task.workerKey)
            ?: throw MissingBackgroundWorkerException(task.workerKey)

        when (val cadence = task.cadence) {
            BackgroundTaskCadence.OneTime -> scheduleOneTime(task, workerClass)
            is BackgroundTaskCadence.Periodic -> schedulePeriodic(task, cadence, workerClass)
        }
    }

    override fun schedule(chain: BackgroundTaskChain) {
        val requests = chain.tasks.map { task ->
            require(task.cadence == BackgroundTaskCadence.OneTime) {
                "Background task chains only support one-time tasks: ${task.uniqueName}"
            }
            val workerClass = workerRegistry.workerClass(task.workerKey)
                ?: throw MissingBackgroundWorkerException(task.workerKey)
            task.toOneTimeWorkRequest(workerClass)
        }

        var continuation = workManager.beginUniqueWork(
            chain.uniqueName,
            when (chain.policy) {
                ExistingBackgroundTaskPolicy.Keep -> ExistingWorkPolicy.KEEP
                ExistingBackgroundTaskPolicy.Replace,
                ExistingBackgroundTaskPolicy.Update,
                -> ExistingWorkPolicy.REPLACE
            },
            requests.first(),
        )

        requests.drop(1).forEach { request ->
            continuation = continuation.then(request)
        }

        continuation.enqueue()
    }

    override fun cancel(uniqueName: String) {
        workManager.cancelUniqueWork(uniqueName)
        workManager.cancelAllWorkByTag(uniqueName)
    }

    override fun isRunning(uniqueName: String): Boolean {
        return workManager.getWorkInfosForUniqueWork(uniqueName)
            .get()
            .hasSingleRunningWork()
    }

    override fun isScheduled(uniqueName: String): Boolean {
        return workManager.getWorkInfosForUniqueWork(uniqueName)
            .get()
            .any { !it.state.isFinished }
    }

    override fun isRunningWithTag(tag: String): Boolean {
        return workManager.getWorkInfosByTag(tag)
            .get()
            .any { it.state == WorkInfo.State.RUNNING }
    }

    override fun runningTasksWithTag(tag: String): List<BackgroundTaskInfo> {
        return workManager.getWorkInfosByTag(tag)
            .get()
            .filter { it.state == WorkInfo.State.RUNNING }
            .map { workInfo ->
                BackgroundTaskInfo(
                    id = workInfo.id.toString(),
                    tags = workInfo.tags,
                )
            }
    }

    override fun cancelTask(task: BackgroundTaskInfo) {
        workManager.cancelWorkById(UUID.fromString(task.id))
    }

    override fun isRunningFlow(uniqueName: String): Flow<Boolean> {
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueName)
            .asFlow()
            .map { workInfos -> workInfos.hasSingleRunningWork() }
            .distinctUntilChanged()
    }

    override fun prune() {
        workManager.pruneWork()
    }

    private fun scheduleOneTime(
        task: BackgroundTask,
        workerClass: Class<out ListenableWorker>,
    ) {
        val request = task.toOneTimeWorkRequest(workerClass)

        workManager.enqueueUniqueWork(
            task.uniqueName,
            when (task.policy) {
                ExistingBackgroundTaskPolicy.Keep -> ExistingWorkPolicy.KEEP
                ExistingBackgroundTaskPolicy.Replace,
                ExistingBackgroundTaskPolicy.Update,
                -> ExistingWorkPolicy.REPLACE
            },
            request,
        )
    }

    private fun BackgroundTask.toOneTimeWorkRequest(
        workerClass: Class<out ListenableWorker>,
    ): OneTimeWorkRequest {
        val requestBuilder = OneTimeWorkRequest.Builder(workerClass)
            .applyTaskConfiguration(this)

        expeditedPolicy?.let { expeditedPolicy ->
            requestBuilder.setExpedited(expeditedPolicy.toWorkManagerOutOfQuotaPolicy())
        }

        return requestBuilder.build()
    }

    private fun schedulePeriodic(
        task: BackgroundTask,
        cadence: BackgroundTaskCadence.Periodic,
        workerClass: Class<out ListenableWorker>,
    ) {
        require(task.expeditedPolicy == null) {
            "Periodic background tasks cannot be expedited: ${task.uniqueName}"
        }

        val requestBuilder = if (cadence.flexInterval != null) {
            PeriodicWorkRequest.Builder(
                workerClass,
                cadence.repeatInterval.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
                cadence.flexInterval.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            )
        } else {
            PeriodicWorkRequest.Builder(
                workerClass,
                cadence.repeatInterval.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            )
        }

        val request = requestBuilder
            .applyTaskConfiguration(task)
            .build()

        workManager.enqueueUniquePeriodicWork(
            task.uniqueName,
            when (task.policy) {
                ExistingBackgroundTaskPolicy.Keep -> ExistingPeriodicWorkPolicy.KEEP
                ExistingBackgroundTaskPolicy.Replace -> ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
                ExistingBackgroundTaskPolicy.Update -> ExistingPeriodicWorkPolicy.UPDATE
            },
            request,
        )
    }

    private fun <T : androidx.work.WorkRequest.Builder<T, *>> T.applyTaskConfiguration(task: BackgroundTask): T {
        addTag(task.uniqueName)
        task.tags.forEach(::addTag)
        setConstraints(task.constraints.toWorkManagerConstraints())
        if (task.inputData.values.isNotEmpty()) {
            setInputData(task.inputData.toWorkManagerData())
        }
        if (task.initialDelay > Duration.ZERO) {
            setInitialDelay(task.initialDelay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        }
        task.backoffCriteria?.let { backoffCriteria ->
            setBackoffCriteria(
                backoffCriteria.policy.toWorkManagerBackoffPolicy(),
                backoffCriteria.delay.inWholeMilliseconds,
                TimeUnit.MILLISECONDS,
            )
        }
        return this
    }

    private fun BackgroundTaskConstraints.toWorkManagerConstraints(): Constraints {
        return Constraints.Builder()
            .apply {
                if (requiresWifi) {
                    val networkRequestBuilder = NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    if (network == BackgroundNetworkConstraint.Unmetered) {
                        networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                    }
                    setRequiredNetworkRequest(networkRequestBuilder.build(), network.toWifiFallbackNetworkType())
                } else {
                    setRequiredNetworkType(network.toWorkManagerNetworkType())
                }
            }
            .setRequiresCharging(requiresCharging)
            .setRequiresBatteryNotLow(requiresBatteryNotLow)
            .build()
    }

    private fun BackgroundTaskInputData.toWorkManagerData(): Data {
        return Data.Builder().apply {
            values.forEach { (key, value) ->
                when (value) {
                    is BackgroundTaskInputValue.BooleanValue -> putBoolean(key, value.value)
                    is BackgroundTaskInputValue.IntValue -> putInt(key, value.value)
                    is BackgroundTaskInputValue.LongValue -> putLong(key, value.value)
                    is BackgroundTaskInputValue.FloatValue -> putFloat(key, value.value)
                    is BackgroundTaskInputValue.DoubleValue -> putDouble(key, value.value)
                    is BackgroundTaskInputValue.StringValue -> putString(key, value.value)
                    is BackgroundTaskInputValue.BooleanArrayValue -> putBooleanArray(key, value.value)
                    is BackgroundTaskInputValue.IntArrayValue -> putIntArray(key, value.value)
                    is BackgroundTaskInputValue.LongArrayValue -> putLongArray(key, value.value)
                    is BackgroundTaskInputValue.FloatArrayValue -> putFloatArray(key, value.value)
                    is BackgroundTaskInputValue.DoubleArrayValue -> putDoubleArray(key, value.value)
                    is BackgroundTaskInputValue.StringArrayValue -> putStringArray(key, value.value)
                }
            }
        }.build()
    }

    private fun BackgroundTaskBackoffPolicy.toWorkManagerBackoffPolicy(): BackoffPolicy {
        return when (this) {
            BackgroundTaskBackoffPolicy.Linear -> BackoffPolicy.LINEAR
            BackgroundTaskBackoffPolicy.Exponential -> BackoffPolicy.EXPONENTIAL
        }
    }

    private fun BackgroundTaskOutOfQuotaPolicy.toWorkManagerOutOfQuotaPolicy(): OutOfQuotaPolicy {
        return when (this) {
            BackgroundTaskOutOfQuotaPolicy.RunAsNonExpedited -> OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
            BackgroundTaskOutOfQuotaPolicy.Drop -> OutOfQuotaPolicy.DROP_WORK_REQUEST
        }
    }

    private fun BackgroundNetworkConstraint.toWorkManagerNetworkType(): NetworkType {
        return when (this) {
            BackgroundNetworkConstraint.NotRequired -> NetworkType.NOT_REQUIRED
            BackgroundNetworkConstraint.Connected -> NetworkType.CONNECTED
            BackgroundNetworkConstraint.Unmetered -> NetworkType.UNMETERED
        }
    }

    private fun BackgroundNetworkConstraint.toWifiFallbackNetworkType(): NetworkType {
        return when (this) {
            BackgroundNetworkConstraint.NotRequired,
            BackgroundNetworkConstraint.Connected,
            -> NetworkType.CONNECTED
            BackgroundNetworkConstraint.Unmetered -> NetworkType.UNMETERED
        }
    }

    private fun List<WorkInfo>.hasSingleRunningWork(): Boolean {
        return count { it.state == WorkInfo.State.RUNNING } == 1
    }

    private fun <T> androidx.lifecycle.LiveData<T>.asFlow(): Flow<T> = callbackFlow {
        val observer = Observer<T> { value -> trySend(value) }
        observeForever(observer)
        awaitClose { removeObserver(observer) }
    }
}
