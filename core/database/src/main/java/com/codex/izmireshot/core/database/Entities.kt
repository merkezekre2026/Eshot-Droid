package com.codex.izmireshot.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.codex.izmireshot.core.model.FavoriteType

@Entity(tableName = "bus_lines", indices = [Index("lineNo"), Index("normalizedName")])
data class BusLineEntity(
    @PrimaryKey val lineNo: Int,
    val name: String,
    val normalizedName: String,
    val routeDescription: String,
    val description: String,
    val start: String,
    val end: String,
    val outboundHours: String,
    val inboundHours: String,
)

@Entity(tableName = "bus_stops", indices = [Index("stopId"), Index("normalizedName")])
data class BusStopEntity(
    @PrimaryKey val stopId: Int,
    val name: String,
    val normalizedName: String,
    val latitude: Double,
    val longitude: Double,
    val servingLines: String,
)

@Entity(
    tableName = "route_points",
    primaryKeys = ["lineNo", "direction", "sequence"],
    indices = [Index("lineNo", "direction")]
)
data class RoutePointEntity(
    val lineNo: Int,
    val direction: Int,
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
)

@Entity(
    tableName = "departure_times",
    primaryKeys = ["lineNo", "tariffId", "sequence"],
    indices = [Index("lineNo")]
)
data class DepartureTimeEntity(
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

@Entity(tableName = "announcements", primaryKeys = ["lineNo", "title"])
data class AnnouncementEntity(
    val lineNo: Int,
    val title: String,
    val startsAt: String?,
    val endsAt: String?,
)

@Entity(tableName = "connection_lines", primaryKeys = ["connectionTypeId", "lineNo"])
data class ConnectionLineEntity(
    val connectionTypeId: Int,
    val lineNo: Int,
    val lineName: String,
    val description: String,
)

@Entity(tableName = "favorites", primaryKeys = ["type", "targetId"])
data class FavoriteEntity(
    val type: FavoriteType,
    val targetId: String,
    val title: String,
    val subtitle: String,
    val createdAtMillis: Long,
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val id: String,
    val type: FavoriteType,
    val title: String,
    val subtitle: String,
    val searchedAtMillis: Long,
)

@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey val source: String,
    val lastSuccessEpochMillis: Long,
    val lastError: String?,
)
