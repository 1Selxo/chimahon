package eu.kanade.tachiyomi.source.model

internal fun String.urlHostOrNull(): String? {
    if (any(Char::isWhitespace)) return null
    val schemeEnd = indexOf("://")
    if (schemeEnd <= 0) return null

    val authorityStart = schemeEnd + 3
    val authorityEnd = indexOfAny(charArrayOf('/', '?', '#'), authorityStart)
        .takeIf { it >= 0 }
        ?: length
    val authority = substring(authorityStart, authorityEnd)
        .substringAfterLast('@')
    if (authority.isEmpty()) return null

    return if (authority.startsWith('[')) {
        authority.substringAfter('[').substringBefore(']').takeIf(String::isNotEmpty)
    } else {
        authority.substringBefore(':').takeIf(String::isNotEmpty)
    }
}

internal expect fun String.pathQueryAndFragment(): String

internal fun String.nativePathQueryAndFragment(): String {
    if (any(Char::isWhitespace)) return this

    val schemeEnd = indexOf("://")
    if (schemeEnd < 0) return this

    val pathStart = indexOfAny(charArrayOf('/', '?', '#'), schemeEnd + 3)
    if (pathStart < 0) return ""

    return when (this[pathStart]) {
        '/', '?' -> substring(pathStart)
        '#' -> substring(pathStart)
        else -> this
    }
}
