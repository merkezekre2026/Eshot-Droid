package com.codex.izmireshot.core.common

import org.junit.Assert.assertEquals
import org.junit.Test

class TurkishTextTest {
    @Test
    fun normalizesTurkishSearchText() {
        assertEquals("ucyol cigli izban", TurkishText.normalizeForSearch("Üçyol ÇİĞLİ İZBAN"))
    }
}
