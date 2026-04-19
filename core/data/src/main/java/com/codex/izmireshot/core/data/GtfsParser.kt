package com.codex.izmireshot.core.data

import java.util.zip.ZipInputStream

class GtfsParser(private val csvParser: CsvParser = CsvParser()) {
    fun parse(zipBytes: ByteArray): GtfsFeed {
        val files = mutableMapOf<String, String>()
        ZipInputStream(zipBytes.inputStream()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.endsWith(".txt")) {
                    files[entry.name.substringAfterLast('/')] = zip.readBytes().toString(Charsets.UTF_8)
                }
                entry = zip.nextEntry
            }
        }
        return GtfsFeed(
            routes = files["routes.txt"]?.let(csvParser::parse).orEmpty(),
            stops = files["stops.txt"]?.let(csvParser::parse).orEmpty(),
            trips = files["trips.txt"]?.let(csvParser::parse).orEmpty(),
            stopTimes = files["stop_times.txt"]?.let(csvParser::parse).orEmpty(),
            shapes = files["shapes.txt"]?.let(csvParser::parse).orEmpty(),
            calendar = files["calendar.txt"]?.let(csvParser::parse).orEmpty(),
            calendarDates = files["calendar_dates.txt"]?.let(csvParser::parse).orEmpty(),
        )
    }
}

data class GtfsFeed(
    val routes: List<Map<String, String>>,
    val stops: List<Map<String, String>>,
    val trips: List<Map<String, String>>,
    val stopTimes: List<Map<String, String>>,
    val shapes: List<Map<String, String>>,
    val calendar: List<Map<String, String>>,
    val calendarDates: List<Map<String, String>>,
)
