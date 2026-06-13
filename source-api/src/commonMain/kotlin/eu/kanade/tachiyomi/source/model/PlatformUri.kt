package eu.kanade.tachiyomi.source.model

expect abstract class PlatformUri {
    abstract fun getHost(): String?

    abstract override fun toString(): String
}

expect fun platformUri(value: String): PlatformUri
