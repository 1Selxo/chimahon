package eu.kanade.tachiyomi.source.model

actual typealias PlatformUri = android.net.Uri

actual fun platformUri(value: String): PlatformUri = android.net.Uri.parse(value)
