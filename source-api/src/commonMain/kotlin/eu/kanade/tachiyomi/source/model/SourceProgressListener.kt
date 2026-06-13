package eu.kanade.tachiyomi.source.model

expect interface SourceProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}
