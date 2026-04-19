package com.codex.izmireshot.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class OfficialCsvAdaptersTest {
    @Test
    fun mapsOfficialStopRows() {
        val rows = listOf(
            mapOf(
                "DURAK_ID" to "12345",
                "DURAK_ADI" to "Çankaya",
                "ENLEM" to "38.421",
                "BOYLAM" to "27.133",
                "DURAKTAN_GECEN_HATLAR" to "5, 7, 202",
            ),
        )

        val stop = OfficialCsvAdapters.stops(rows).single()

        assertEquals(12345, stop.stopId)
        assertEquals(listOf(5, 7, 202), stop.servingLines)
    }
}
