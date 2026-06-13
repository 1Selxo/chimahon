package eu.kanade.tachiyomi.source.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UrlPartsTest {
    @Test
    fun extractsHostFromAbsoluteUrls() {
        assertEquals("example.com", "https://user@example.com:8443/path".urlHostOrNull())
        assertEquals("2001:db8::1", "https://[2001:db8::1]/chapter".urlHostOrNull())
        assertNull("/relative/path".urlHostOrNull())
    }

    @Test
    fun stripsSchemeAndAuthorityWithoutLosingSuffixes() {
        assertEquals("/manga/1?lang=en#page", "https://example.com/manga/1?lang=en#page".pathQueryAndFragment())
        assertEquals("?page=2", "https://example.com?page=2".pathQueryAndFragment())
        assertEquals("/relative/path", "/relative/path".pathQueryAndFragment())
        assertEquals("https://example.com/a path", "https://example.com/a path".pathQueryAndFragment())
    }
}
