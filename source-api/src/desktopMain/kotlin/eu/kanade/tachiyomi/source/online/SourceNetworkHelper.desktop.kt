package eu.kanade.tachiyomi.source.online

import okhttp3.OkHttpClient

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Mobile Safari/537.36"

internal actual fun sourceNetworkContext(
    delegateClientProvider: () -> OkHttpClient?,
): SourceNetworkContext {
    val client = OkHttpClient()
    return object : SourceNetworkContext() {
        override val client: OkHttpClient
            get() = delegateClientProvider() ?: client

        override fun defaultUserAgentProvider(): String = DESKTOP_USER_AGENT
    }
}
