package eu.kanade.tachiyomi.source.online

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

internal actual fun createNativeScriptHttpClient(): HttpClient = HttpClient(CIO) {
    expectSuccess = false
    followRedirects = true
}
