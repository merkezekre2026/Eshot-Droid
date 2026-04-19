package com.codex.izmireshot.core.model

enum class Direction(val id: Int, val label: String) {
    Outbound(1, "Gidiş"),
    Inbound(2, "Dönüş");

    companion object {
        fun fromId(id: Int): Direction = entries.firstOrNull { it.id == id } ?: Outbound
    }
}

enum class FavoriteType { Line, Stop }

data class BusLine(
    val lineNo: Int,
    val name: String,
    val routeDescription: String,
    val description: String,
    val start: String,
    val end: String,
    val outboundHours: String = "",
    val inboundHours: String = "",
)

data class BusStop(
    val stopId: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val servingLines: List<Int>,
)

data class RoutePoint(
    val lineNo: Int,
    val direction: Direction,
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
)

data class DepartureTime(
    val lineNo: Int,
    val tariffId: Int,
    val sequence: Int,
    val outboundTime: String?,
    val inboundTime: String?,
    val outboundAccessible: Boolean,
    val inboundAccessible: Boolean,
    val outboundBike: Boolean,
    val inboundBike: Boolean,
    val outboundElectric: Boolean,
    val inboundElectric: Boolean,
)

data class Announcement(
    val lineNo: Int,
    val title: String,
    val startsAt: String?,
    val endsAt: String?,
)

data class ConnectionLine(
    val connectionTypeId: Int,
    val lineNo: Int,
    val lineName: String,
    val description: String,
)

data class LiveBus(
    val lineNo: Int?,
    val vehicleId: String?,
    val latitude: Double,
    val longitude: Double,
    val speed: Double?,
    val lastUpdated: String?,
)

data class NearbyStop(
    val stop: BusStop,
    val distanceMeters: Double,
    val source: NearbySource,
)

enum class NearbySource { OfficialApi, LocalCache }

data class Favorite(
    val id: String,
    val type: FavoriteType,
    val title: String,
    val subtitle: String,
)

data class RecentSearch(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: FavoriteType,
    val searchedAtMillis: Long,
)

data class SyncStatus(
    val source: String,
    val lastSuccessEpochMillis: Long,
    val lastError: String?,
)

data class LineDetail(
    val line: BusLine,
    val departures: List<DepartureTime>,
    val outboundStops: List<BusStop>,
    val inboundStops: List<BusStop>,
    val outboundShape: List<RoutePoint>,
    val inboundShape: List<RoutePoint>,
    val announcements: List<Announcement>,
    val liveBuses: List<LiveBus>,
)

data class StopDetail(
    val stop: BusStop,
    val servingLines: List<BusLine>,
    val approachingBuses: List<LiveBus>,
)
