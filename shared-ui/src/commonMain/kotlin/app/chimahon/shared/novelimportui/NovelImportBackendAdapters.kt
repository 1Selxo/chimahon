package app.chimahon.shared.novelimportui

import app.chimahon.shared.novelui.ChimahonNovelBackendError

fun ChimahonNovelImportQueueLoadResult.toNovelImportUiState(
    searchQuery: String = "",
    selectedFileIds: Set<String> = queue.files.filter { it.selected }.mapTo(mutableSetOf()) { it.ref.id },
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelImportUiState {
    return queue.toNovelImportUiState(
        searchQuery = searchQuery,
        selectedFileIds = selectedFileIds,
        errorMessage = errors.toNovelImportErrorMessage() ?: queue.errorMessage,
        formatEpochMillis = formatEpochMillis,
    )
}

fun ChimahonNovelImportQueueSnapshot.toNovelImportUiState(
    searchQuery: String = "",
    selectedFileIds: Set<String> = files.filter { it.selected }.mapTo(mutableSetOf()) { it.ref.id },
    errorMessage: String? = this.errorMessage,
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelImportUiState {
    return ChimahonNovelImportUiState(
        title = title,
        subtitle = subtitle,
        sources = sources.map { it.toNovelImportSourceUiModel() }.ifEmpty { ChimahonNovelImportDefaults.sources },
        selectedSourceId = selectedSourceId,
        files = files.map { it.toNovelImportFileUiModel(formatEpochMillis) },
        metadataPreview = files.firstNotNullOfOrNull { it.metadata }?.toNovelImportMetadataUiModel(formatEpochMillis),
        volumes = volumes.map { it.toNovelImportVolumeUiModel() },
        chapters = chapters.map { it.toNovelImportChapterUiModel() },
        options = options.toNovelImportOptionsUiState(),
        progress = progress.toNovelImportProgressUiModel(),
        summary = summary.toNovelImportSummaryUiModel(),
        stage = stage.toNovelImportStage(),
        searchQuery = searchQuery,
        selectedFileIds = selectedFileIds,
        errorMessage = errorMessage,
        warningMessage = warningMessage,
    )
}

fun ChimahonNovelImportSourceRecord.toNovelImportSourceUiModel(): ChimahonNovelImportSourceUiModel {
    return ChimahonNovelImportSourceUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        kind = kind,
        enabled = enabled,
        recommended = recommended,
        badgeLabel = badgeLabel,
        queuedCount = queuedCount,
    )
}

fun ChimahonNovelImportFileRecord.toNovelImportFileUiModel(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelImportFileUiModel {
    return ChimahonNovelImportFileUiModel(
        id = ref.id,
        displayName = ref.displayName,
        sourceId = sourceId,
        pathLabel = ref.path ?: ref.uri,
        sizeLabel = ref.sizeBytes?.toNovelImportByteLabel(),
        mimeTypeLabel = ref.mimeType,
        modifiedLabel = ref.modifiedEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        status = status.toNovelImportFileStatus(),
        progress = progress.toNovelImportProgressUiModel(),
        metadata = metadata?.toNovelImportMetadataUiModel(formatEpochMillis),
        errorMessage = errorMessage,
        warningMessage = warningMessage,
        selected = selected,
        duplicate = duplicate,
        canRemove = canRemove,
        canRetry = status == ChimahonNovelImportBackendFileStatus.Failed,
    )
}

fun ChimahonNovelImportMetadataRecord.toNovelImportMetadataUiModel(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelImportMetadataUiModel {
    return ChimahonNovelImportMetadataUiModel(
        id = id,
        title = title,
        subtitle = subtitle,
        authors = authors,
        language = language,
        publisher = publisher,
        description = description,
        coverRef = coverRef,
        sourceFileName = sourceFileName,
        categoryLabels = categoryLabels,
        tags = tags,
        volumeCount = volumeCount,
        chapterCount = chapterCount,
        characterCount = characterCount,
        wordCountLabel = wordCount?.let { "$it words" },
        createdLabel = createdEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        modifiedLabel = modifiedEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        existingLibraryMatch = existingLibraryMatch?.toNovelImportLibraryMatchUiModel(formatEpochMillis),
    )
}

fun ChimahonNovelImportLibraryMatchRecord.toNovelImportLibraryMatchUiModel(
    formatEpochMillis: (Long) -> String? = { null },
): ChimahonNovelImportLibraryMatchUiModel {
    return ChimahonNovelImportLibraryMatchUiModel(
        bookId = bookId,
        title = title,
        categoryLabels = categoryLabels,
        lastReadLabel = lastReadEpochMillis.takeIf { it > 0L }?.let(formatEpochMillis),
        progressLabel = progressPercent?.let { "${it.coerceIn(0, 100)}%" },
        matchKind = matchKind,
    )
}

fun ChimahonNovelImportVolumeRecord.toNovelImportVolumeUiModel(): ChimahonNovelImportVolumeUiModel {
    return ChimahonNovelImportVolumeUiModel(
        id = id,
        title = title,
        sourceOrder = sourceOrder,
        chapterIds = chapterIds,
        expanded = expanded,
        selectedCount = selectedCount,
        warningMessage = warningMessage,
    )
}

fun ChimahonNovelImportChapterRecord.toNovelImportChapterUiModel(): ChimahonNovelImportChapterUiModel {
    return ChimahonNovelImportChapterUiModel(
        id = id,
        title = title,
        href = href,
        sourceOrder = sourceOrder,
        spineIndex = spineIndex,
        volumeId = volumeId,
        characterStart = characterStart,
        characterCount = characterCount,
        wordCountLabel = wordCount?.let { "$it words" },
        status = status.toNovelImportChapterStatus(),
        selected = selected,
        imageOnly = imageOnly,
        warnings = warnings,
        duplicateOfId = duplicateOfId,
    )
}

fun ChimahonNovelImportBackendOptions.toNovelImportOptionsUiState(): ChimahonNovelImportOptionsUiState {
    return ChimahonNovelImportOptionsUiState(
        destinationCategoryId = destinationCategoryId,
        destinationCategoryLabel = destinationCategoryLabel,
        dictionaryProfileName = dictionaryProfileName,
        overwriteExisting = overwriteExisting,
        keepOriginalFileName = keepOriginalFileName,
        importSelectedChaptersOnly = importSelectedChaptersOnly,
        showSkippedChapters = showSkippedChapters,
    )
}

fun ChimahonNovelImportBackendProgress.toNovelImportProgressUiModel(): ChimahonNovelImportProgressUiModel {
    val byteLabel = when {
        processedBytes != null && totalBytes != null ->
            "${processedBytes.toNovelImportByteLabel()} / ${totalBytes.toNovelImportByteLabel()}"
        processedBytes != null -> processedBytes.toNovelImportByteLabel()
        totalBytes != null -> totalBytes.toNovelImportByteLabel()
        else -> null
    }

    return ChimahonNovelImportProgressUiModel(
        fraction = normalizedFraction.toFloat(),
        processedCount = processedCount,
        totalCount = totalCount,
        percentLabel = "${(normalizedFraction * 100.0).toInt().coerceIn(0, 100)}%",
        bytesLabel = byteLabel,
        speedLabel = speedBytesPerSecond?.let { "${it.toNovelImportByteLabel()}/s" },
        etaLabel = etaSeconds?.toNovelImportEtaLabel(),
        message = message,
    )
}

fun ChimahonNovelImportBackendSummary.toNovelImportSummaryUiModel(): ChimahonNovelImportSummaryUiModel {
    return ChimahonNovelImportSummaryUiModel(
        total = total,
        imported = imported,
        failed = failed,
        skipped = skipped,
        duplicates = duplicates,
    )
}

private fun ChimahonNovelImportBackendStage.toNovelImportStage(): ChimahonNovelImportStage {
    return when (this) {
        ChimahonNovelImportBackendStage.Idle -> ChimahonNovelImportStage.Idle
        ChimahonNovelImportBackendStage.ChoosingSource -> ChimahonNovelImportStage.ChoosingSource
        ChimahonNovelImportBackendStage.Scanning -> ChimahonNovelImportStage.Scanning
        ChimahonNovelImportBackendStage.ReadingMetadata -> ChimahonNovelImportStage.ReadingMetadata
        ChimahonNovelImportBackendStage.Ready -> ChimahonNovelImportStage.Ready
        ChimahonNovelImportBackendStage.Importing -> ChimahonNovelImportStage.Importing
        ChimahonNovelImportBackendStage.Paused -> ChimahonNovelImportStage.Paused
        ChimahonNovelImportBackendStage.Complete -> ChimahonNovelImportStage.Complete
        ChimahonNovelImportBackendStage.Failed -> ChimahonNovelImportStage.Failed
        ChimahonNovelImportBackendStage.Canceled -> ChimahonNovelImportStage.Canceled
    }
}

private fun ChimahonNovelImportBackendFileStatus.toNovelImportFileStatus(): ChimahonNovelImportFileStatus {
    return when (this) {
        ChimahonNovelImportBackendFileStatus.Staged -> ChimahonNovelImportFileStatus.Staged
        ChimahonNovelImportBackendFileStatus.Queued -> ChimahonNovelImportFileStatus.Queued
        ChimahonNovelImportBackendFileStatus.ReadingMetadata -> ChimahonNovelImportFileStatus.ReadingMetadata
        ChimahonNovelImportBackendFileStatus.Ready -> ChimahonNovelImportFileStatus.Ready
        ChimahonNovelImportBackendFileStatus.Importing -> ChimahonNovelImportFileStatus.Importing
        ChimahonNovelImportBackendFileStatus.Imported -> ChimahonNovelImportFileStatus.Imported
        ChimahonNovelImportBackendFileStatus.Skipped -> ChimahonNovelImportFileStatus.Skipped
        ChimahonNovelImportBackendFileStatus.Warning -> ChimahonNovelImportFileStatus.Warning
        ChimahonNovelImportBackendFileStatus.Failed -> ChimahonNovelImportFileStatus.Failed
        ChimahonNovelImportBackendFileStatus.Canceled -> ChimahonNovelImportFileStatus.Canceled
    }
}

private fun ChimahonNovelImportBackendChapterStatus.toNovelImportChapterStatus(): ChimahonNovelImportChapterStatus {
    return when (this) {
        ChimahonNovelImportBackendChapterStatus.Ready -> ChimahonNovelImportChapterStatus.Ready
        ChimahonNovelImportBackendChapterStatus.Selected -> ChimahonNovelImportChapterStatus.Selected
        ChimahonNovelImportBackendChapterStatus.Skipped -> ChimahonNovelImportChapterStatus.Skipped
        ChimahonNovelImportBackendChapterStatus.Duplicate -> ChimahonNovelImportChapterStatus.Duplicate
        ChimahonNovelImportBackendChapterStatus.Warning -> ChimahonNovelImportChapterStatus.Warning
        ChimahonNovelImportBackendChapterStatus.Error -> ChimahonNovelImportChapterStatus.Error
    }
}

private fun List<ChimahonNovelBackendError>.toNovelImportErrorMessage(): String? {
    return firstOrNull()?.let { error -> error.message ?: error.code.name }
}

private fun Long.toNovelImportByteLabel(): String {
    val safeBytes = coerceAtLeast(0L)
    val kib = 1024.0
    val mib = kib * 1024.0
    val gib = mib * 1024.0
    return when {
        safeBytes >= gib -> "${(safeBytes / gib).oneDecimal()} GiB"
        safeBytes >= mib -> "${(safeBytes / mib).oneDecimal()} MiB"
        safeBytes >= kib -> "${(safeBytes / kib).oneDecimal()} KiB"
        else -> "$safeBytes B"
    }
}

private fun Long.toNovelImportEtaLabel(): String {
    val safeSeconds = coerceAtLeast(0L)
    val minutes = safeSeconds / 60L
    val seconds = safeSeconds % 60L
    return if (minutes > 0L) "${minutes}m ${seconds}s left" else "${seconds}s left"
}

private fun Double.oneDecimal(): String {
    val scaled = (this * 10.0).toInt()
    val whole = scaled / 10
    val decimal = scaled % 10
    return "$whole.$decimal"
}
