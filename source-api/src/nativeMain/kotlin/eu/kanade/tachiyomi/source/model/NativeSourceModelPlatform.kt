package eu.kanade.tachiyomi.source.model

actual interface PlatformSerializable

actual interface SourceProgressListener {
    actual fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}

actual abstract class PlatformUri {
    actual abstract fun getHost(): String?

    actual abstract override fun toString(): String
}

actual fun platformUri(value: String): PlatformUri = NativePlatformUri(value)

internal actual fun String.pathQueryAndFragment(): String = nativePathQueryAndFragment()

private class NativePlatformUri(
    private val value: String,
) : PlatformUri() {
    override fun getHost(): String? = value.urlHostOrNull()

    override fun toString(): String = value
}
