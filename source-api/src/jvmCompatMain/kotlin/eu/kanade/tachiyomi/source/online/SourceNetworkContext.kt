package eu.kanade.tachiyomi.source.online

import okhttp3.OkHttpClient

abstract class SourceNetworkContext {
    abstract val client: OkHttpClient

    abstract fun defaultUserAgentProvider(): String
}

internal expect fun sourceNetworkContext(
    delegateClientProvider: () -> OkHttpClient?,
): SourceNetworkContext
