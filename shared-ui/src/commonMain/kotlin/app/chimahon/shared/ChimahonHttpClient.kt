package app.chimahon.shared

import io.ktor.client.HttpClient

internal expect fun createChimahonHttpClient(): HttpClient
