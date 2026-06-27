package app.chimahon.shared.anime

data class ChimahonAnimeExtensionRepoSummary(
    val baseUrl: String,
    val name: String,
    val shortName: String? = null,
    val website: String = "",
    val signingKeyFingerprint: String = "",
) {
    val displayName: String
        get() = shortName?.takeIf { it.isNotBlank() } ?: name

    val normalizedBaseUrl: String
        get() = baseUrl.trim().trimEnd('/')
}

data class ChimahonAnimeExtensionRepoData(
    val repos: List<ChimahonAnimeExtensionRepoSummary>,
    val isRefreshing: Boolean = false,
    val dialog: ChimahonAnimeExtensionRepoDialog? = null,
) {
    val isEmpty: Boolean
        get() = repos.isEmpty()

    val count: Int
        get() = repos.size
}

sealed interface ChimahonAnimeExtensionRepoDialog {
    data object Create : ChimahonAnimeExtensionRepoDialog
    data class Delete(val baseUrl: String) : ChimahonAnimeExtensionRepoDialog
    data class Confirm(val url: String) : ChimahonAnimeExtensionRepoDialog
    data class Conflict(
        val oldRepo: ChimahonAnimeExtensionRepoSummary,
        val newRepo: ChimahonAnimeExtensionRepoSummary,
    ) : ChimahonAnimeExtensionRepoDialog
}

sealed interface ChimahonAnimeExtensionRepoEvent {
    data object InvalidUrl : ChimahonAnimeExtensionRepoEvent
    data object RepoAlreadyExists : ChimahonAnimeExtensionRepoEvent
    data class DuplicateFingerprint(
        val oldRepo: ChimahonAnimeExtensionRepoSummary,
        val newRepo: ChimahonAnimeExtensionRepoSummary,
    ) : ChimahonAnimeExtensionRepoEvent
}
