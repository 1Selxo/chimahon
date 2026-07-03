package eu.kanade.tachiyomi.source.online

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createNativeScriptHttpClient(): HttpClient = HttpClient(Darwin) {
    expectSuccess = false
    followRedirects = true
}
