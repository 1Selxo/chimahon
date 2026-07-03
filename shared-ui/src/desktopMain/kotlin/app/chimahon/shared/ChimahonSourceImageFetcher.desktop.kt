package app.chimahon.shared

import eu.kanade.tachiyomi.network.HttpException
import eu.kanade.tachiyomi.network.awaitSuccess
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.coroutines.CancellationException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

internal actual suspend fun loadSourcePageImage(
    source: CatalogueSource,
    page: Page,
    options: SourceImageFetchOptions,
): ByteArray {
    val httpSource = source as? HttpSource
        ?: throw SourceImageFetchException(
            failure = SourceImageFetchFailure.UNSUPPORTED_SOURCE,
            sourceId = source.id,
            sourceName = source.name,
            pageIndex = page.index,
            detail = "desktop requires HttpSource",
        )

    if (page.imageUrl.isNullOrBlank()) {
        page.status = Page.State.LoadPage
        page.imageUrl = try {
            httpSource.getImageUrl(page)
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

    page.status = Page.State.DownloadImage
    return try {
        val response = httpSource.executeDesktopImageRequest(page, imageUrl, options)
        response.use {
            val bytes = it.body.bytes()
            try {
                normalizeDesktopReaderImageBytes(
                    bytes = bytes,
                    contentType = it.header("Content-Type"),
                    source = source,
                    page = page,
                    imageUrl = imageUrl,
                )
            } catch (failure: SourceImageFetchException) {
                val originImageUrl = imageUrl.directImageUrlFromKnownProxy()
                    ?: throw failure
                val retryResponse = httpSource.executeDesktopImageRequest(page, originImageUrl, options)
                retryResponse.use { retry ->
                    normalizeDesktopReaderImageBytes(
                        bytes = retry.body.bytes(),
                        contentType = retry.header("Content-Type"),
                        source = source,
                        page = page,
                        imageUrl = originImageUrl,
                    )
                }
            }
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: HttpException) {
        throw SourceImageFetchException(
            failure = SourceImageFetchFailure.HTTP_ERROR,
            sourceId = source.id,
            sourceName = source.name,
            pageIndex = page.index,
            httpStatus = error.code,
            cause = error,
        )
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

private suspend fun HttpSource.executeDesktopImageRequest(
    page: Page,
    imageUrl: String,
    options: SourceImageFetchOptions,
) = if (options.headers.isEmpty() && options.referer == null) {
    val originalImageUrl = page.imageUrl
    page.imageUrl = imageUrl
    try {
        getImage(page)
    } finally {
        page.imageUrl = originalImageUrl
    }
} else {
    val headers = headers.newBuilder().apply {
        options.effectiveHeaders().forEach { (name, value) ->
            set(name, value)
        }
    }.build()
    val request = Request.Builder()
        .url(imageUrl)
        .headers(headers)
        .get()
        .build()
    client.newCall(request).awaitSuccess()
}

private fun normalizeDesktopReaderImageBytes(
    bytes: ByteArray,
    contentType: String?,
    source: CatalogueSource,
    page: Page,
    imageUrl: String,
): ByteArray {
    if (bytes.isDesktopReaderDecodable()) return bytes

    ImageIO.setUseCache(false)
    val image = runCatching { ImageIO.read(ByteArrayInputStream(bytes)) }.getOrNull()
    if (image != null) {
        return image.toPngBytes()
    }

    throw SourceImageFetchException(
        failure = SourceImageFetchFailure.DOWNLOAD,
        sourceId = source.id,
        sourceName = source.name,
        pageIndex = page.index,
        detail = buildString {
            append("desktop could not decode image bytes")
            contentType?.takeIf(String::isNotBlank)?.let { append(" (Content-Type $it)") }
            append("; signature ")
            append(bytes.imageByteSignature())
            bytes.textPreview()?.let {
                append("; body ")
                append(it)
            }
            append("; url ")
            append(imageUrl)
        },
    )
}

private fun ByteArray.isDesktopReaderDecodable(): Boolean {
    return runCatching {
        org.jetbrains.skia.Image.makeFromEncoded(this).close()
    }.isSuccess
}

private fun BufferedImage.toPngBytes(): ByteArray {
    val normalized = if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
        this
    } else {
        BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).also { output ->
            val graphics = output.createGraphics()
            try {
                graphics.color = Color.WHITE
                graphics.fillRect(0, 0, width, height)
                graphics.drawImage(this, 0, 0, null)
            } finally {
                graphics.dispose()
            }
        }
    }
    return ByteArrayOutputStream().use { output ->
        ImageIO.write(normalized, "png", output)
        output.toByteArray()
    }
}

private fun ByteArray.imageByteSignature(): String {
    return take(16).joinToString(separator = " ") { byte ->
        (byte.toInt() and 0xff).toString(16).uppercase().padStart(2, '0')
    }.ifBlank { "empty" }
}

private fun ByteArray.textPreview(): String? {
    if (isEmpty()) return null
    val previewBytes = take(96).toByteArray()
    val printable = previewBytes.all { byte ->
        val value = byte.toInt() and 0xff
        value == 0x09 || value == 0x0a || value == 0x0d || value in 0x20..0x7e
    }
    if (!printable) return null
    val preview = previewBytes.decodeToString()
        .replace('\r', ' ')
        .replace('\n', ' ')
        .trim()
    return preview.takeIf(String::isNotBlank)
}

private fun String.directImageUrlFromKnownProxy(): String? {
    val url = toHttpUrlOrNull() ?: return null
    val host = url.host.lowercase()
    if (host == "wsrv.nl" || host.endsWith(".wsrv.nl") || host == "images.weserv.nl") {
        return url.queryParameter("url")
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?.let { proxied ->
                when {
                    proxied.startsWith("http://", ignoreCase = true) ||
                        proxied.startsWith("https://", ignoreCase = true) -> proxied
                    else -> "https://$proxied"
                }
            }
    }

    if (host.matches(WORDPRESS_IMAGE_PROXY_HOST)) {
        val encodedPath = url.encodedPath.trimStart('/')
        val slashIndex = encodedPath.indexOf('/')
        if (slashIndex > 0) {
            val originHost = encodedPath.substring(0, slashIndex)
            val originPath = encodedPath.substring(slashIndex + 1)
            if (originHost.contains('.') && originPath.isNotBlank()) {
                return "https://$originHost/$originPath"
            }
        }
    }

    return null
}

private val WORDPRESS_IMAGE_PROXY_HOST = Regex("""i\d+\.wp\.com""")
