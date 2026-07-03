package app.chimahon.shared.anime

const val CHIMAHON_LOCAL_ANIME_MEDIA_SOURCE_ID: Long = 0L

val CHIMAHON_LOCAL_ANIME_DEFAULT_MEDIA_EXTENSIONS: Set<String> = setOf(
    "3gp",
    "avi",
    "flv",
    "m4v",
    "mkv",
    "mov",
    "mp4",
    "mpeg",
    "mpg",
    "ogm",
    "ogv",
    "ts",
    "webm",
    "wmv",
)

val CHIMAHON_LOCAL_ANIME_DEFAULT_SUBTITLE_EXTENSIONS: Set<String> = setOf(
    "ass",
    "smi",
    "srt",
    "ssa",
    "sub",
    "vtt",
)

val CHIMAHON_LOCAL_ANIME_DEFAULT_THUMBNAIL_EXTENSIONS: Set<String> = setOf(
    "avif",
    "jpeg",
    "jpg",
    "png",
    "webp",
)

val CHIMAHON_LOCAL_ANIME_DEFAULT_METADATA_FILE_NAMES: Set<String> = setOf(
    "anime.json",
    "details.json",
    "metadata.json",
    "series.json",
)

data class ChimahonLocalAnimeMediaPlatformCapabilities(
    val platformName: String = "",
    val localSourceAvailable: Boolean = false,
    val canPickRootFolders: Boolean = false,
    val canPickMediaFiles: Boolean = false,
    val canPersistAccessGrants: Boolean = false,
    val canScanFolders: Boolean = false,
    val canWatchFolders: Boolean = false,
    val canReadMediaMetadata: Boolean = false,
    val canWriteSidecarMetadata: Boolean = false,
    val canGenerateThumbnails: Boolean = false,
    val canServePlaybackUris: Boolean = false,
    val canOpenExternalPlayer: Boolean = false,
    val maxConcurrentScans: Int = 1,
    val supportedMediaExtensions: Set<String> = CHIMAHON_LOCAL_ANIME_DEFAULT_MEDIA_EXTENSIONS,
    val supportedSubtitleExtensions: Set<String> = CHIMAHON_LOCAL_ANIME_DEFAULT_SUBTITLE_EXTENSIONS,
    val supportedThumbnailExtensions: Set<String> = CHIMAHON_LOCAL_ANIME_DEFAULT_THUMBNAIL_EXTENSIONS,
    val supportedMetadataFileNames: Set<String> = CHIMAHON_LOCAL_ANIME_DEFAULT_METADATA_FILE_NAMES,
) {
    val enabledCount: Int
        get() = listOf(
            localSourceAvailable,
            canPickRootFolders,
            canPickMediaFiles,
            canPersistAccessGrants,
            canScanFolders,
            canWatchFolders,
            canReadMediaMetadata,
            canWriteSidecarMetadata,
            canGenerateThumbnails,
            canServePlaybackUris,
            canOpenExternalPlayer,
        ).count { it }

    val hasAnyLocalMediaCapability: Boolean
        get() = enabledCount > 0

    val normalizedMaxConcurrentScans: Int
        get() = maxConcurrentScans.coerceAtLeast(1)

    fun supportsMediaFile(fileName: String): Boolean {
        return fileName.toChimahonLocalAnimeMediaExtension() in supportedMediaExtensions.normalizedLocalAnimeMediaExtensions()
    }

    fun supportsSubtitleFile(fileName: String): Boolean {
        return fileName.toChimahonLocalAnimeMediaExtension() in supportedSubtitleExtensions.normalizedLocalAnimeMediaExtensions()
    }

    fun supportsThumbnailFile(fileName: String): Boolean {
        return fileName.toChimahonLocalAnimeMediaExtension() in supportedThumbnailExtensions.normalizedLocalAnimeMediaExtensions()
    }

    fun supportsMetadataFile(fileName: String): Boolean {
        val normalizedFileName = fileName.trim()
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .lowercase()
        return normalizedFileName in supportedMetadataFileNames.map { it.trim().lowercase() }.toSet()
    }
}

data class ChimahonLocalAnimeMediaPlatformReport(
    val capabilities: ChimahonLocalAnimeMediaPlatformCapabilities = ChimahonLocalAnimeMediaPlatformCapabilities(),
    val roots: List<ChimahonLocalAnimeRootFolder> = emptyList(),
    val constraints: List<ChimahonLocalAnimeMediaPlatformConstraint> = emptyList(),
    val reportedAtMillis: Long = 0L,
) {
    val localAnimeAvailable: Boolean
        get() = capabilities.localSourceAvailable || roots.any { it.accessible }

    val needsUserAction: Boolean
        get() = constraints.any { it.recoverable }
}

data class ChimahonLocalAnimeMediaPlatformConstraint(
    val code: ChimahonLocalAnimeMediaPlatformConstraintCode,
    val message: String? = null,
    val recoverable: Boolean = true,
)

enum class ChimahonLocalAnimeMediaPlatformConstraintCode {
    PermissionRequired,
    ReadOnlyRoot,
    ScopedStorage,
    BackgroundScanUnavailable,
    MetadataReaderUnavailable,
    ThumbnailGeneratorUnavailable,
    ExternalToolMissing,
    UnsupportedPlatform,
    Unknown,
}

data class ChimahonLocalAnimeRootFolder(
    val id: String,
    val name: String,
    val uri: String,
    val pathHint: String? = null,
    val accessible: Boolean = true,
    val writable: Boolean = false,
    val removable: Boolean = false,
    val persistedAccessGrant: Boolean = false,
    val animeCount: Int = 0,
    val mediaFileCount: Int = 0,
    val lastScannedAtMillis: Long = 0L,
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val displayName: String
        get() = name.ifBlank { pathHint?.substringAfterLast('/')?.substringAfterLast('\\') ?: "Local anime" }

    val healthy: Boolean
        get() = accessible && problems.none { it.severity == ChimahonLocalAnimeMediaProblemSeverity.Error }
}

data class ChimahonLocalAnimeFolder(
    val id: String,
    val rootId: String,
    val name: String,
    val uri: String,
    val pathHint: String? = null,
    val kind: ChimahonLocalAnimeFolderKind = ChimahonLocalAnimeFolderKind.Unknown,
    val parentId: String? = null,
    val depth: Int = 0,
    val hidden: Boolean = false,
    val childFolderCount: Int = 0,
    val mediaFileCount: Int = 0,
    val sidecarFileCount: Int = 0,
    val thumbnailCount: Int = 0,
    val sizeBytes: Long = 0L,
    val lastModifiedAtMillis: Long = 0L,
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val displayName: String
        get() = name.ifBlank { pathHint?.substringAfterLast('/')?.substringAfterLast('\\') ?: "Folder" }

    val hasMedia: Boolean
        get() = mediaFileCount > 0

    val importable: Boolean
        get() = hasMedia || childFolderCount > 0
}

enum class ChimahonLocalAnimeFolderKind {
    Root,
    Anime,
    Season,
    Episode,
    Extras,
    Unknown,
}

data class ChimahonLocalAnimeMediaEntry(
    val id: String,
    val rootId: String,
    val folderId: String,
    val title: String,
    val folderName: String = title,
    val folderUri: String = "",
    val metadata: ChimahonLocalAnimeMediaMetadata = ChimahonLocalAnimeMediaMetadata(),
    val thumbnail: ChimahonLocalAnimeThumbnail? = null,
    val background: ChimahonLocalAnimeThumbnail? = null,
    val seasons: List<ChimahonLocalAnimeSeason> = emptyList(),
    val looseEpisodes: List<ChimahonLocalAnimeEpisode> = emptyList(),
    val discoveredAtMillis: Long = 0L,
    val lastModifiedAtMillis: Long = 0L,
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val displayTitle: String
        get() = metadata.title?.takeIf { it.isNotBlank() } ?: title.ifBlank { folderName.ifBlank { "Untitled anime" } }

    val allEpisodes: List<ChimahonLocalAnimeEpisode>
        get() = seasons.flatMap { it.episodes } + looseEpisodes

    val episodeCount: Int
        get() = allEpisodes.size

    val mediaFileCount: Int
        get() = allEpisodes.sumOf { it.mediaFiles.size }
}

data class ChimahonLocalAnimeSeason(
    val id: String,
    val animeId: String,
    val folderId: String? = null,
    val title: String? = null,
    val seasonNumber: Double = 1.0,
    val sourceOrder: Long = 0L,
    val metadata: ChimahonLocalAnimeMediaMetadata = ChimahonLocalAnimeMediaMetadata(),
    val thumbnail: ChimahonLocalAnimeThumbnail? = null,
    val episodes: List<ChimahonLocalAnimeEpisode> = emptyList(),
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val displayTitle: String
        get() = metadata.title?.takeIf { it.isNotBlank() }
            ?: title?.takeIf { it.isNotBlank() }
            ?: seasonNumber.takeIf { it > 0.0 }?.let { "Season ${it.toChimahonLocalAnimeMediaNumber()}" }
            ?: "Season"

    val episodeCount: Int
        get() = episodes.size
}

data class ChimahonLocalAnimeEpisode(
    val id: String,
    val animeId: String,
    val seasonId: String? = null,
    val folderId: String? = null,
    val title: String? = null,
    val episodeNumber: Double = -1.0,
    val sourceOrder: Long = 0L,
    val summary: String? = null,
    val airDateMillis: Long = 0L,
    val mediaFiles: List<ChimahonLocalAnimeMediaFile> = emptyList(),
    val sidecars: List<ChimahonLocalAnimeMediaSidecarFile> = emptyList(),
    val thumbnail: ChimahonLocalAnimeThumbnail? = null,
    val metadata: ChimahonLocalAnimeMediaMetadata = ChimahonLocalAnimeMediaMetadata(),
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val displayTitle: String
        get() = metadata.title?.takeIf { it.isNotBlank() }
            ?: title?.takeIf { it.isNotBlank() }
            ?: episodeNumber.takeIf { it >= 0.0 }?.let { "Episode ${it.toChimahonLocalAnimeMediaNumber()}" }
            ?: "Episode"

    val primaryMediaFile: ChimahonLocalAnimeMediaFile?
        get() = mediaFiles.firstOrNull { it.role == ChimahonLocalAnimeMediaFileRole.Primary } ?: mediaFiles.firstOrNull()

    val durationSeconds: Long
        get() = metadata.durationSeconds.takeIf { it > 0L } ?: (primaryMediaFile?.durationSeconds ?: 0L)

    val playable: Boolean
        get() = primaryMediaFile?.playable == true
}

data class ChimahonLocalAnimeMediaMetadata(
    val title: String? = null,
    val originalTitle: String? = null,
    val sortTitle: String? = null,
    val summary: String? = null,
    val language: String? = null,
    val genres: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val releaseYear: Int? = null,
    val seasonNumber: Double? = null,
    val episodeNumber: Double? = null,
    val durationSeconds: Long = 0L,
    val poster: ChimahonLocalAnimeThumbnail? = null,
    val background: ChimahonLocalAnimeThumbnail? = null,
    val credits: List<ChimahonLocalAnimeMediaCredit> = emptyList(),
    val externalIds: List<ChimahonLocalAnimeMediaExternalId> = emptyList(),
    val source: ChimahonLocalAnimeMediaMetadataSource = ChimahonLocalAnimeMediaMetadataSource.Unknown,
    val sourceUri: String? = null,
    val updatedAtMillis: Long = 0L,
) {
    val displayTitle: String?
        get() = title?.takeIf { it.isNotBlank() } ?: originalTitle?.takeIf { it.isNotBlank() }
}

data class ChimahonLocalAnimeMediaCredit(
    val name: String,
    val role: ChimahonLocalAnimeMediaCreditRole = ChimahonLocalAnimeMediaCreditRole.Other,
)

enum class ChimahonLocalAnimeMediaCreditRole {
    Studio,
    Director,
    Writer,
    VoiceActor,
    Translator,
    Encoder,
    Other,
}

data class ChimahonLocalAnimeMediaExternalId(
    val provider: String,
    val value: String,
    val url: String? = null,
)

enum class ChimahonLocalAnimeMediaMetadataSource {
    FileName,
    FolderName,
    SidecarFile,
    EmbeddedTags,
    Manual,
    RemoteMatch,
    Unknown,
}

data class ChimahonLocalAnimeMediaFile(
    val id: String,
    val uri: String,
    val fileName: String,
    val displayName: String = fileName,
    val extension: String = fileName.toChimahonLocalAnimeMediaExtension(),
    val mimeType: String? = null,
    val role: ChimahonLocalAnimeMediaFileRole = ChimahonLocalAnimeMediaFileRole.Primary,
    val sizeBytes: Long = 0L,
    val durationSeconds: Long = 0L,
    val lastModifiedAtMillis: Long = 0L,
    val container: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val frameRate: Double? = null,
    val videoCodec: String? = null,
    val audioCodec: String? = null,
    val bitrate: Long = 0L,
    val metadata: ChimahonLocalAnimeMediaMetadata = ChimahonLocalAnimeMediaMetadata(),
    val tracks: List<ChimahonLocalAnimeMediaTrackInfo> = emptyList(),
    val chapters: List<ChimahonLocalAnimeMediaChapter> = emptyList(),
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val normalizedExtension: String
        get() = extension.toChimahonLocalAnimeMediaExtension()

    val playable: Boolean
        get() = role != ChimahonLocalAnimeMediaFileRole.Subtitle &&
            role != ChimahonLocalAnimeMediaFileRole.Thumbnail &&
            role != ChimahonLocalAnimeMediaFileRole.Metadata

    val resolutionLabel: String?
        get() = when {
            width > 0 && height > 0 -> "${width}x$height"
            height > 0 -> "${height}p"
            else -> null
        }
}

enum class ChimahonLocalAnimeMediaFileRole {
    Primary,
    Alternate,
    Trailer,
    Opening,
    Ending,
    Extra,
    Subtitle,
    Thumbnail,
    Metadata,
    Unknown,
}

data class ChimahonLocalAnimeMediaTrackInfo(
    val id: String,
    val kind: ChimahonLocalAnimeMediaTrackKind,
    val title: String? = null,
    val language: String? = null,
    val codec: String? = null,
    val externalUri: String? = null,
    val default: Boolean = false,
    val forced: Boolean = false,
    val delaySeconds: Double = 0.0,
)

enum class ChimahonLocalAnimeMediaTrackKind {
    Video,
    Audio,
    Subtitle,
    Attachment,
    Chapter,
    Unknown,
}

data class ChimahonLocalAnimeMediaChapter(
    val id: String,
    val title: String,
    val startSeconds: Long,
    val endSeconds: Long? = null,
) {
    val durationSeconds: Long
        get() = endSeconds?.let { (it - startSeconds).coerceAtLeast(0L) } ?: 0L
}

data class ChimahonLocalAnimeMediaSidecarFile(
    val id: String,
    val uri: String,
    val fileName: String,
    val kind: ChimahonLocalAnimeMediaSidecarKind = ChimahonLocalAnimeMediaSidecarKind.Unknown,
    val language: String? = null,
    val mimeType: String? = null,
    val sizeBytes: Long = 0L,
    val lastModifiedAtMillis: Long = 0L,
) {
    val extension: String
        get() = fileName.toChimahonLocalAnimeMediaExtension()
}

enum class ChimahonLocalAnimeMediaSidecarKind {
    Metadata,
    Subtitle,
    Chapters,
    Thumbnail,
    Font,
    Unknown,
}

data class ChimahonLocalAnimeThumbnail(
    val id: String,
    val uri: String,
    val fileName: String = "",
    val kind: ChimahonLocalAnimeThumbnailKind = ChimahonLocalAnimeThumbnailKind.Poster,
    val mimeType: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val sizeBytes: Long = 0L,
    val source: ChimahonLocalAnimeThumbnailSource = ChimahonLocalAnimeThumbnailSource.Unknown,
    val sourceMediaFileId: String? = null,
    val timecodeSeconds: Long? = null,
    val generatedAtMillis: Long = 0L,
    val lastModifiedAtMillis: Long = 0L,
) {
    val aspectRatio: Float
        get() = if (width > 0 && height > 0) width.toFloat() / height.toFloat() else 0f

    val generated: Boolean
        get() = source == ChimahonLocalAnimeThumbnailSource.Generated
}

enum class ChimahonLocalAnimeThumbnailKind {
    Poster,
    Background,
    SeasonPoster,
    EpisodePreview,
    Folder,
    FrameGrab,
    Unknown,
}

enum class ChimahonLocalAnimeThumbnailSource {
    Embedded,
    SidecarFile,
    Generated,
    Remote,
    Manual,
    Unknown,
}

data class ChimahonLocalAnimeMediaProblem(
    val code: ChimahonLocalAnimeMediaProblemCode,
    val message: String? = null,
    val subjectId: String? = null,
    val uri: String? = null,
    val severity: ChimahonLocalAnimeMediaProblemSeverity = ChimahonLocalAnimeMediaProblemSeverity.Warning,
)

enum class ChimahonLocalAnimeMediaProblemCode {
    PermissionDenied,
    RootMissing,
    UnsupportedFormat,
    InvalidMetadata,
    DuplicateEpisode,
    MissingMedia,
    ThumbnailUnavailable,
    MetadataUnavailable,
    PlatformUnsupported,
    Unknown,
}

enum class ChimahonLocalAnimeMediaProblemSeverity {
    Info,
    Warning,
    Error,
}

data class ChimahonLocalAnimeScanRequest(
    val rootId: String? = null,
    val rootUri: String? = null,
    val recursive: Boolean = true,
    val includeHidden: Boolean = false,
    val includeMetadata: Boolean = true,
    val includeThumbnails: Boolean = true,
    val refreshCache: Boolean = false,
)

data class ChimahonLocalAnimeScanResult(
    val roots: List<ChimahonLocalAnimeRootFolder> = emptyList(),
    val folders: List<ChimahonLocalAnimeFolder> = emptyList(),
    val anime: List<ChimahonLocalAnimeMediaEntry> = emptyList(),
    val scannedAtMillis: Long = 0L,
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val successful: Boolean
        get() = problems.none { it.severity == ChimahonLocalAnimeMediaProblemSeverity.Error }

    val totalEpisodeCount: Int
        get() = anime.sumOf { it.episodeCount }

    val totalMediaFileCount: Int
        get() = anime.sumOf { it.mediaFileCount }
}

data class ChimahonLocalAnimeRootFoldersResult(
    val roots: List<ChimahonLocalAnimeRootFolder> = emptyList(),
    val capabilities: ChimahonLocalAnimeMediaPlatformCapabilities = ChimahonLocalAnimeMediaPlatformCapabilities(),
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val successful: Boolean
        get() = problems.none { it.severity == ChimahonLocalAnimeMediaProblemSeverity.Error }
}

data class ChimahonLocalAnimeMediaMetadataRequest(
    val uri: String,
    val mediaFileId: String? = null,
    val includeEmbeddedTags: Boolean = true,
    val includeSidecars: Boolean = true,
    val forceRefresh: Boolean = false,
)

data class ChimahonLocalAnimeMediaMetadataResult(
    val metadata: ChimahonLocalAnimeMediaMetadata? = null,
    val mediaFile: ChimahonLocalAnimeMediaFile? = null,
    val sidecars: List<ChimahonLocalAnimeMediaSidecarFile> = emptyList(),
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val successful: Boolean
        get() = problems.none { it.severity == ChimahonLocalAnimeMediaProblemSeverity.Error }

    val found: Boolean
        get() = metadata != null || mediaFile != null
}

data class ChimahonLocalAnimeThumbnailRequest(
    val animeId: String? = null,
    val seasonId: String? = null,
    val episodeId: String? = null,
    val mediaFileId: String? = null,
    val uri: String? = null,
    val kind: ChimahonLocalAnimeThumbnailKind = ChimahonLocalAnimeThumbnailKind.EpisodePreview,
    val timecodeSeconds: Long? = null,
    val widthHint: Int = 0,
    val heightHint: Int = 0,
    val preferCached: Boolean = true,
)

data class ChimahonLocalAnimeThumbnailResult(
    val thumbnail: ChimahonLocalAnimeThumbnail? = null,
    val cacheKey: String? = null,
    val problems: List<ChimahonLocalAnimeMediaProblem> = emptyList(),
) {
    val successful: Boolean
        get() = problems.none { it.severity == ChimahonLocalAnimeMediaProblemSeverity.Error }

    val found: Boolean
        get() = thumbnail != null
}

fun String.toChimahonLocalAnimeMediaExtension(): String {
    val fileName = trim()
        .substringAfterLast('/', this)
        .substringAfterLast('\\')
        .trim()
    return fileName
        .substringAfterLast('.', fileName)
        .trim()
        .trimStart('.')
        .lowercase()
}

fun Double.toChimahonLocalAnimeMediaNumber(): String {
    val intValue = toInt()
    return if (this == intValue.toDouble()) {
        intValue.toString()
    } else {
        toString().trimEnd('0').trimEnd('.')
    }
}

private fun Set<String>.normalizedLocalAnimeMediaExtensions(): Set<String> {
    return map { it.toChimahonLocalAnimeMediaExtension() }.filter(String::isNotBlank).toSet()
}
