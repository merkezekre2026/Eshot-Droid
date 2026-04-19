package com.codex.izmireshot.core.data

import com.codex.izmireshot.core.common.TurkishText
import com.codex.izmireshot.core.database.AnnouncementEntity
import com.codex.izmireshot.core.database.BusLineEntity
import com.codex.izmireshot.core.database.BusStopEntity
import com.codex.izmireshot.core.database.ConnectionLineEntity
import com.codex.izmireshot.core.database.DepartureTimeEntity
import com.codex.izmireshot.core.database.FavoriteEntity
import com.codex.izmireshot.core.database.RecentSearchEntity
import com.codex.izmireshot.core.database.RoutePointEntity
import com.codex.izmireshot.core.model.Announcement
import com.codex.izmireshot.core.model.BusLine
import com.codex.izmireshot.core.model.BusStop
import com.codex.izmireshot.core.model.ConnectionLine
import com.codex.izmireshot.core.model.DepartureTime
import com.codex.izmireshot.core.model.Direction
import com.codex.izmireshot.core.model.Favorite
import com.codex.izmireshot.core.model.FavoriteType
import com.codex.izmireshot.core.model.RecentSearch
import com.codex.izmireshot.core.model.RoutePoint

fun BusLineEntity.toModel() = BusLine(lineNo, name, routeDescription, description, start, end, outboundHours, inboundHours)

fun BusLine.toEntity() = BusLineEntity(
    lineNo = lineNo,
    name = name,
    normalizedName = TurkishText.normalizeForSearch("$lineNo $name $routeDescription $start $end"),
    routeDescription = routeDescription,
    description = description,
    start = start,
    end = end,
    outboundHours = outboundHours,
    inboundHours = inboundHours,
)

fun BusStopEntity.toModel() = BusStop(
    stopId = stopId,
    name = name,
    latitude = latitude,
    longitude = longitude,
    servingLines = TurkishText.parseLineNumbers(servingLines),
)

fun BusStop.toEntity() = BusStopEntity(
    stopId = stopId,
    name = name,
    normalizedName = TurkishText.normalizeForSearch("$stopId $name ${servingLines.joinToString(" ")}"),
    latitude = latitude,
    longitude = longitude,
    servingLines = servingLines.joinToString(","),
)

fun RoutePointEntity.toModel() = RoutePoint(lineNo, Direction.fromId(direction), sequence, latitude, longitude)
fun RoutePoint.toEntity() = RoutePointEntity(lineNo, direction.id, sequence, latitude, longitude)

fun DepartureTimeEntity.toModel() = DepartureTime(
    lineNo, tariffId, sequence, outboundTime, inboundTime, outboundAccessible, inboundAccessible,
    outboundBike, inboundBike, outboundElectric, inboundElectric
)

fun DepartureTime.toEntity() = DepartureTimeEntity(
    lineNo, tariffId, sequence, outboundTime, inboundTime, outboundAccessible, inboundAccessible,
    outboundBike, inboundBike, outboundElectric, inboundElectric
)

fun AnnouncementEntity.toModel() = Announcement(lineNo, title, startsAt, endsAt)
fun Announcement.toEntity() = AnnouncementEntity(lineNo, title, startsAt, endsAt)

fun ConnectionLine.toEntity() = ConnectionLineEntity(connectionTypeId, lineNo, lineName, description)

fun FavoriteEntity.toModel() = Favorite(targetId, type, title, subtitle)
fun Favorite.toEntity(now: Long) = FavoriteEntity(type, id, title, subtitle, now)

fun RecentSearchEntity.toModel() = RecentSearch(id, title, subtitle, type, searchedAtMillis)
fun RecentSearch.toEntity() = RecentSearchEntity(id, type, title, subtitle, searchedAtMillis)

fun favoriteForLine(line: BusLine) = Favorite(line.lineNo.toString(), FavoriteType.Line, "${line.lineNo} ${line.name}", line.routeDescription)
fun favoriteForStop(stop: BusStop) = Favorite(stop.stopId.toString(), FavoriteType.Stop, stop.name, "Durak no: ${stop.stopId}")
