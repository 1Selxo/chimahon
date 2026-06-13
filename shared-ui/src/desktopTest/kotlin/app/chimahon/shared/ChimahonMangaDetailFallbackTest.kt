package app.chimahon.shared

import eu.kanade.tachiyomi.source.model.SManga
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChimahonMangaDetailFallbackTest {
    @Test
    fun fillsMissingDetailFieldsFromSourceListingSeed() {
        val seed = SManga(
            url = "/manga/solo-leveling",
            title = "Solo Leveling",
            author = "Chugong",
            status = 0,
            thumbnail_url = "https://example.test/cover.jpg",
            initialized = false,
        )
        val parsed = SManga.create().apply {
            title = "Ore dake Level Up na Ken"
        }

        parsed.withSeedFallback(seed)

        assertEquals(seed.url, parsed.url)
        assertEquals("Ore dake Level Up na Ken", parsed.title)
        assertEquals(seed.author, parsed.author)
        assertEquals(seed.thumbnail_url, parsed.thumbnail_url)
        assertTrue(parsed.initialized)
    }

    @Test
    fun keepsParsedFieldsButUsesSeedTitleWhenParserLeavesItBlank() {
        val seed = SManga(
            url = "/manga/chimahon",
            title = "Chimahon",
            author = "Seed Author",
            status = 0,
            thumbnail_url = "seed-cover",
            initialized = false,
        )
        val parsed = SManga.create().apply {
            author = "Parsed Author"
            thumbnail_url = "parsed-cover"
        }

        parsed.withSeedFallback(seed)

        assertEquals(seed.url, parsed.url)
        assertEquals(seed.title, parsed.title)
        assertEquals("Parsed Author", parsed.author)
        assertEquals("parsed-cover", parsed.thumbnail_url)
        assertTrue(parsed.initialized)
    }
}
