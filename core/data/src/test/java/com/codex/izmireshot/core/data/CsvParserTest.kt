package com.codex.izmireshot.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class CsvParserTest {
    @Test
    fun parsesQuotedTurkishCsv() {
        val csv = """
            HAT_NO,HAT_ADI,GUZERGAH_ACIKLAMA
            5,"ÜÇYOL - ÇİĞLİ","Konak, Alsancak"
        """.trimIndent()

        val rows = CsvParser().parse(csv)

        assertEquals(1, rows.size)
        assertEquals("ÜÇYOL - ÇİĞLİ", rows.first()["HAT_ADI"])
        assertEquals("Konak, Alsancak", rows.first()["GUZERGAH_ACIKLAMA"])
    }

    @Test
    fun parsesSemicolonCsv() {
        val csv = """
            DURAK_ID;DURAK_ADI;ENLEM;BOYLAM
            100;İskele;38,42;27,12
        """.trimIndent()

        val rows = CsvParser().parse(csv)

        assertEquals("İskele", rows.first()["DURAK_ADI"])
        assertEquals("38,42", rows.first()["ENLEM"])
    }
}
