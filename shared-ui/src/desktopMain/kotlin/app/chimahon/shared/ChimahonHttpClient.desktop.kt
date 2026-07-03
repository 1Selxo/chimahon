package app.chimahon.shared

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

internal actual fun createChimahonHttpClient(): HttpClient = HttpClient(CIO) {
    expectSuccess = false
    followRedirects = true
}
