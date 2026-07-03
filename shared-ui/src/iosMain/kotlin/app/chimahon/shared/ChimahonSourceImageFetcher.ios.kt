package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.ScriptHttpSource
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException

internal actual suspend fun loadSourcePageImage(
    source: CatalogueSource,
    page: Page,
    options: SourceImageFetchOptions,
): ByteArray {
    val scriptSource = source as? ScriptHttpSource
        ?: throw SourceImageFetchException(
            failure = SourceImageFetchFailure.UNSUPPORTED_SOURCE,
            sourceId = source.id,
            sourceName = source.name,
            pageIndex = page.index,
            detail = "iOS requires ScriptHttpSource",
        )

    if (page.imageUrl.isNullOrBlank()) {
        page.status = Page.State.LoadPage
        page.imageUrl = try {
            scriptSource.getImageUrl(page)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            throw SourceImageFetchException(
                failure = SourceImageFetchFailure.IMAGE_URL_RESOLUTION,
                sourceId = source.id,
                sourceName = source.name,
                pageIndex = page.index,
                detail = error.message,
                cause = error,
            )
        }
    }

    val imageUrl = page.imageUrl?.takeIf(String::isNotBlank)
        ?: throw SourceImageFetchException(
            failure = SourceImageFetchFailure.INVALID_REQUEST,
            sourceId = source.id,
            sourceName = source.name,
            pageIndex = page.index,
            detail = "source returned a blank image URL",
        )
    val resolvedImageUrl = imageUrl.resolveAgainstBaseUrl(scriptSource.baseUrl)
    if (resolvedImageUrl != imageUrl) {
        page.imageUrl = resolvedImageUrl
    }

    page.status = Page.State.DownloadImage
    return try {
        if (options.headers.isEmpty() && options.referer == null) {
            scriptSource.getImageBytes(page)
        } else {
            val requestHeaders = options.effectiveHeaders()
            val response = sourceImageHttpClient.request(resolvedImageUrl) {
                headers {
                    requestHeaders.forEach { (name, value) ->
                        append(name, value)
                    }
                    if (requestHeaders.keys.none { it.equals(HttpHeaders.UserAgent, ignoreCase = true) }) {
                        append(HttpHeaders.UserAgent, IOS_IMAGE_USER_AGENT)
                    }
                    if (requestHeaders.keys.none { it.equals(HttpHeaders.Accept, ignoreCase = true) }) {
                        append(HttpHeaders.Accept, IOS_IMAGE_ACCEPT)
                    }
                }
            }
            if (!response.status.isSuccess()) {
                throw SourceImageFetchException(
                    failure = SourceImageFetchFailure.HTTP_ERROR,
                    sourceId = source.id,
                    sourceName = source.name,
                    pageIndex = page.index,
                    httpStatus = response.status.value,
                )
            }
            response.readRawBytes()
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: SourceImageFetchException) {
        throw error
    } catch (error: Throwable) {
        throw SourceImageFetchException(
            failure = SourceImageFetchFailure.DOWNLOAD,
            sourceId = source.id,
            sourceName = source.name,
            pageIndex = page.index,
            detail = error.message,
            cause = error,
        )
    }
}

private val sourceImageHttpClient = createChimahonHttpClient()

private const val IOS_IMAGE_USER_AGENT = "Chimahon iOS"
private const val IOS_IMAGE_ACCEPT = "image/avif,image/webp,image/*,*/*;q=0.8"

private fun String.resolveAgainstBaseUrl(baseUrl: String): String {
    val candidate = trim()
    if (candidate.startsWith("//")) return "https:$candidate"
    if (candidate.hasUrlScheme()) return candidate

    val root = baseUrl.trimEnd('/')
    if (root.isBlank()) return candidate
    return if (candidate.startsWith("/")) {
        val schemeSplit = root.indexOf("://")
        if (schemeSplit == -1) {
            "$root$candidate"
        } else {
            val hostStart = schemeSplit + 3
            val hostEnd = root.indexOf('/', startIndex = hostStart).takeIf { it >= 0 } ?: root.length
            root.take(hostEnd) + candidate
        }
    } else {
        "$root/${candidate.trimStart('/')}"
    }
}

private fun String.hasUrlScheme(): Boolean {
    val colon = indexOf(':')
    if (colon <= 0) return false
    val scheme = take(colon)
    return scheme.first().isLetter() &&
        scheme.all { it.isLetterOrDigit() || it == '+' || it == '-' || it == '.' }
}
