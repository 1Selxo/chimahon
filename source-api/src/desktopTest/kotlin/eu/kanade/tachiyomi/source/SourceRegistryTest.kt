package eu.kanade.tachiyomi.source

import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class SourceRegistryTest {
    @Test
    fun registersAndRemovesSourcesById() {
        val first = FakeSource(id = 1, name = "First")
        val replacement = FakeSource(id = 1, name = "Replacement")
        val registry = SourceRegistry(listOf(first))

        assertSame(first, registry.get(1))

        registry.register(replacement)
        assertSame(replacement, registry.get(1))

        registry.unregister(1)
        assertNull(registry.get(1))
    }

    @Test
    fun replacesTheRegistrySnapshot() {
        val registry = SourceRegistry(listOf(FakeSource(1, "Old")))
        val newSources = listOf(
            FakeSource(2, "Second"),
            FakeSource(3, "Third"),
        )

        registry.replaceAll(newSources)

        assertNull(registry.get(1))
        assertEquals(setOf(2L, 3L), registry.sources.value.keys)
    }
}

private class FakeSource(
    override val id: Long,
    override val name: String,
) : Source {
    override suspend fun getMangaDetails(manga: SManga): SManga = manga

    override suspend fun getChapterList(manga: SManga): List<SChapter> = emptyList()

    override suspend fun getPageList(chapter: SChapter): List<Page> = emptyList()

    override suspend fun getRelatedMangaList(
        manga: SManga,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedManga: Pair<String, List<SManga>>, completed: Boolean) -> Unit,
    ) = Unit
}
