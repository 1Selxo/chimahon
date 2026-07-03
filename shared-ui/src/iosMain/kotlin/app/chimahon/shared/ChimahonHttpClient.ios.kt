package app.chimahon.shared

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createChimahonHttpClient(): HttpClient = HttpClient(Darwin) {
    expectSuccess = false
    followRedirects = true
}
