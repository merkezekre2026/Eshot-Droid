package com.codex.izmireshot.core.data

import com.codex.izmireshot.core.common.TurkishText
import com.codex.izmireshot.core.model.Announcement
import com.codex.izmireshot.core.model.BusLine
import com.codex.izmireshot.core.model.BusStop
import com.codex.izmireshot.core.model.ConnectionLine
import com.codex.izmireshot.core.model.DepartureTime
import com.codex.izmireshot.core.model.Direction
import com.codex.izmireshot.core.model.RoutePoint

object OfficialCsvAdapters {
    fun lines(rows: List<Map<String, String>>): List<BusLine> = rows.mapNotNull { row ->
        val lineNo = row.value("HAT_NO").toIntOrNull() ?: return@mapNotNull null
        BusLine(
            lineNo = lineNo,
            name = row.value("HAT_ADI"),
            routeDescription = row.value("GUZERGAH_ACIKLAMA"),
            description = row.value("ACIKLAMA"),
            start = row.value("HAT_BASLANGIC"),
            end = row.value("HAT_BITIS"),
        )
    }

    fun stops(rows: List<Map<String, String>>): List<BusStop> = rows.mapNotNull { row ->
        val stopId = row.value("DURAK_ID").toIntOrNull() ?: return@mapNotNull null
        val lat = row.value("ENLEM").asDoubleOrNull() ?: return@mapNotNull null
        val lon = row.value("BOYLAM").asDoubleOrNull() ?: return@mapNotNull null
        BusStop(
            stopId = stopId,
            name = row.value("DURAK_ADI"),
            latitude = lat,
            longitude = lon,
            servingLines = TurkishText.parseLineNumbers(row.value("DURAKTAN_GECEN_HATLAR")),
        )
    }

    fun routes(rows: List<Map<String, String>>): List<RoutePoint> = rows.mapIndexedNotNull { index, row ->
        val lineNo = row.value("HAT_NO").toIntOrNull() ?: return@mapIndexedNotNull null
        val lat = row.value("ENLEM").asDoubleOrNull() ?: return@mapIndexedNotNull null
        val lon = row.value("BOYLAM").asDoubleOrNull() ?: return@mapIndexedNotNull null
        val direction = Direction.fromId(row.value("YON").toIntOrNull() ?: 1)
        RoutePoint(lineNo, direction, index, lat, lon)
    }

    fun departures(rows: List<Map<String, String>>): List<DepartureTime> = rows.mapNotNull { row ->
        val lineNo = row.value("HAT_NO").toIntOrNull() ?: return@mapNotNull null
        val tariff = row.value("TARIFE_ID").toIntOrNull() ?: 0
        val sequence = row.value("SIRA").toIntOrNull() ?: 0
        DepartureTime(
            lineNo = lineNo,
            tariffId = tariff,
            sequence = sequence,
            outboundTime = row.value("GIDIS_SAATI").ifBlank { null },
            inboundTime = row.value("DONUS_SAATI").ifBlank { null },
            outboundAccessible = row.value("GIDIS_ENGELLI").asBoolean(),
            inboundAccessible = row.value("DONUS_ENGELLI").asBoolean(),
            outboundBike = row.value("GIDIS_BISIKLETLI").asBoolean(),
            inboundBike = row.value("DONUS_BISIKLETLI").asBoolean(),
            outboundElectric = row.value("GIDIS_ELEKTRIKLI").asBoolean(),
            inboundElectric = row.value("DONUS_ELEKTRIKLI").asBoolean(),
        )
    }

    fun announcements(rows: List<Map<String, String>>): List<Announcement> = rows.mapNotNull { row ->
        val lineNo = row.value("HAT_NO").toIntOrNull() ?: return@mapNotNull null
        Announcement(
            lineNo = lineNo,
            title = row.value("BASLIK"),
            startsAt = row.value("BASLAMA_TARIHI").ifBlank { null },
            endsAt = row.value("BITIS_TARIHI").ifBlank { null },
        )
    }

    fun connections(rows: List<Map<String, String>>): List<ConnectionLine> = rows.mapNotNull { row ->
        val typeId = row.value("BAGLANTI_TIPI_ID").toIntOrNull() ?: return@mapNotNull null
        val lineNo = row.value("HAT_NO").toIntOrNull() ?: return@mapNotNull null
        ConnectionLine(typeId, lineNo, row.value("HAT_ADI"), row.value("ACIKLAMA"))
    }

    private fun Map<String, String>.value(key: String): String =
        this[key] ?: this[key.lowercase()] ?: this.entries.firstOrNull { it.key.equals(key, ignoreCase = true) }?.value.orEmpty()

    private fun String.asDoubleOrNull(): Double? = replace(',', '.').toDoubleOrNull()

    private fun String.asBoolean(): Boolean = trim().lowercase() in setOf("1", "true", "evet", "e")
}
