package eu.kanade.tachiyomi.source.online

import android.app.Application
import eu.kanade.tachiyomi.network.NetworkHelper
import exh.log.maybeInjectEHLogger
import okhttp3.OkHttpClient
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

internal actual fun sourceNetworkContext(
    delegateClientProvider: () -> OkHttpClient?,
): SourceNetworkContext {
    val network = Injekt.get<NetworkHelper>()
    val app = Injekt.get<Application>()
    val preferences = Injekt.get<eu.kanade.tachiyomi.network.NetworkPreferences>()
    val sourceNetwork = object : NetworkHelper(app, preferences, network.isDebugBuild) {
        override val client: OkHttpClient
            get() = delegateClientProvider() ?: network.client
                .newBuilder()
                .maybeInjectEHLogger()
                .build()

        @Deprecated("The regular client handles Cloudflare by default")
        override val cloudflareClient: OkHttpClient
            get() = delegateClientProvider() ?: client
    }
    return object : SourceNetworkContext() {
        override val client: OkHttpClient
            get() = sourceNetwork.client

        override fun defaultUserAgentProvider(): String {
            return sourceNetwork.defaultUserAgentProvider()
        }
    }
}
