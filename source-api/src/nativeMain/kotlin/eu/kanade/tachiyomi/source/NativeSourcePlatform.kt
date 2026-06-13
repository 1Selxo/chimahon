package eu.kanade.tachiyomi.source

internal actual fun sourceApiLogError(message: String, throwable: Throwable) {
    println("$message: ${throwable.message.orEmpty()}")
}
