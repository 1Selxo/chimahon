package eu.kanade.tachiyomi.source.model

actual abstract class PlatformUri {
    actual abstract fun getHost(): String?

    actual abstract override fun toString(): String
}

actual fun platformUri(value: String): PlatformUri = DesktopPlatformUri(value)

private class DesktopPlatformUri(
    private val value: String,
) : PlatformUri() {
    override fun getHost(): String? = runCatching { java.net.URI(value).host }.getOrNull()

    override fun toString(): String = value
}
