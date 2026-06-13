package eu.kanade.tachiyomi.source

import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal actual fun sourceApiLogError(message: String, throwable: Throwable) {
    logcat(tag = "SourceApi", priority = LogPriority.ERROR) { "$message: ${throwable.asLog()}" }
}
