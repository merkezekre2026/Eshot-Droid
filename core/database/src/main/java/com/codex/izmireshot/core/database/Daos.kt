package com.codex.izmireshot.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TransportDao {
    @Query("SELECT * FROM bus_lines ORDER BY lineNo")
    fun observeLines(): Flow<List<BusLineEntity>>

    @Query("SELECT * FROM bus_lines WHERE lineNo = :lineNo")
    fun observeLine(lineNo: Int): Flow<BusLineEntity?>

    @Query("SELECT * FROM bus_lines WHERE CAST(lineNo AS TEXT) LIKE '%' || :query || '%' OR normalizedName LIKE '%' || :query || '%' ORDER BY lineNo LIMIT 50")
    fun searchLines(query: String): Flow<List<BusLineEntity>>

    @Query("SELECT * FROM bus_lines WHERE lineNo IN (:lineNumbers) ORDER BY lineNo")
    fun observeLinesByIds(lineNumbers: List<Int>): Flow<List<BusLineEntity>>

    @Query("SELECT * FROM bus_stops WHERE stopId = :stopId")
    fun observeStop(stopId: Int): Flow<BusStopEntity?>

    @Query("SELECT * FROM bus_stops WHERE CAST(stopId AS TEXT) LIKE '%' || :query || '%' OR normalizedName LIKE '%' || :query || '%' ORDER BY name LIMIT 50")
    fun searchStops(query: String): Flow<List<BusStopEntity>>

    @Query("SELECT * FROM bus_stops WHERE ',' || servingLines || ',' LIKE '%,' || :lineNo || ',%' ORDER BY name")
    fun observeStopsServingLine(lineNo: Int): Flow<List<BusStopEntity>>

    @Query("SELECT * FROM bus_stops")
    suspend fun getAllStops(): List<BusStopEntity>

    @Query("SELECT * FROM route_points WHERE lineNo = :lineNo AND direction = :direction ORDER BY sequence")
    fun observeRoute(lineNo: Int, direction: Int): Flow<List<RoutePointEntity>>

    @Query("SELECT * FROM departure_times WHERE lineNo = :lineNo ORDER BY sequence")
    fun observeDepartures(lineNo: Int): Flow<List<DepartureTimeEntity>>

    @Query("SELECT * FROM announcements WHERE lineNo = :lineNo ORDER BY startsAt DESC")
    fun observeAnnouncements(lineNo: Int): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements ORDER BY startsAt DESC")
    fun observeAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM sync_status")
    fun observeSyncStatus(): Flow<List<SyncStatusEntity>>

    @Upsert
    suspend fun upsertLines(lines: List<BusLineEntity>)

    @Upsert
    suspend fun upsertStops(stops: List<BusStopEntity>)

    @Upsert
    suspend fun upsertRoutePoints(points: List<RoutePointEntity>)

    @Upsert
    suspend fun upsertDepartures(departures: List<DepartureTimeEntity>)

    @Upsert
    suspend fun upsertAnnouncements(announcements: List<AnnouncementEntity>)

    @Upsert
    suspend fun upsertConnections(connections: List<ConnectionLineEntity>)

    @Upsert
    suspend fun upsertSyncStatus(status: SyncStatusEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM favorites ORDER BY createdAtMillis DESC")
    fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE type = :type AND targetId = :targetId)")
    fun observeIsFavorite(type: com.codex.izmireshot.core.model.FavoriteType, targetId: String): Flow<Boolean>

    @Upsert
    suspend fun upsertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE type = :type AND targetId = :targetId")
    suspend fun deleteFavorite(type: com.codex.izmireshot.core.model.FavoriteType, targetId: String)

    @Query("SELECT * FROM recent_searches ORDER BY searchedAtMillis DESC LIMIT 10")
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>>

    @Upsert
    suspend fun upsertRecentSearch(search: RecentSearchEntity)
}
