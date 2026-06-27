package eu.kanade.tachiyomi.torrentutils

import eu.kanade.tachiyomi.torrentutils.model.TorrentInfo

actual object TorrentUtils {
    actual fun getTorrentInfo(
        url: String,
        title: String,
    ): TorrentInfo {
        throw UnsupportedOperationException("Torrent server integration is not available on desktop yet.")
    }
}
