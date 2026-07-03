package eu.kanade.tachiyomi.torrentutils.model

data class TorrentFile(
    val path: String,
    val indexFile: Int,
    val size: Long,
    private val torrentHash: String,
    private val trackers: List<String> = emptyList(),
) {
    fun toMagnetURI(): String {
        val trackers = trackers.joinToString("&tr=") { it.encodeMagnetQueryComponent() }
        return "magnet:?xt=urn:btih:$torrentHash${if (trackers.isNotEmpty()) "&tr=$trackers" else ""}&index=$indexFile"
    }
}

private fun String.encodeMagnetQueryComponent(): String {
    return encodeToByteArray().joinToString("") { byte ->
        val unsigned = byte.toInt() and 0xff
        val character = unsigned.toChar()
        if (character.isMagnetQuerySafe()) {
            character.toString()
        } else {
            "%" + unsigned.toString(radix = 16).uppercase().padStart(2, '0')
        }
    }
}

private fun Char.isMagnetQuerySafe(): Boolean {
    return this in 'A'..'Z' ||
        this in 'a'..'z' ||
        this in '0'..'9' ||
        this == '-' ||
        this == '_' ||
        this == '.' ||
        this == '*'
}
