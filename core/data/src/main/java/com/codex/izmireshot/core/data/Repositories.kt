package com.codex.izmireshot.core.data

import com.codex.izmireshot.core.model.Announcement
import com.codex.izmireshot.core.model.BusLine
import com.codex.izmireshot.core.model.BusStop
import com.codex.izmireshot.core.model.Favorite
import com.codex.izmireshot.core.model.FavoriteType
import com.codex.izmireshot.core.model.LineDetail
import com.codex.izmireshot.core.model.LiveBus
import com.codex.izmireshot.core.model.NearbyStop
import com.codex.izmireshot.core.model.RecentSearch
import com.codex.izmireshot.core.model.RoutePoint
import com.codex.izmireshot.core.model.StopDetail
import kotlinx.coroutines.flow.Flow

interface TransportRepository {
    fun searchLines(query: String): Flow<List<BusLine>>
    fun searchStops(query: String): Flow<List<BusStop>>
    fun observeLineDetail(lineNo: Int): Flow<LineDetail?>
    fun observeStopDetail(stopId: Int): Flow<StopDetail?>
    fun observeRoute(lineNo: Int, directionId: Int): Flow<List<RoutePoint>>
    suspend fun refreshAll()
    suspend fun refreshLiveBuses(lineNo: Int): List<LiveBus>
    suspend fun refreshApproachingBuses(stopId: Int): List<LiveBus>
}

interface NearbyRepository {
    suspend fun nearbyStops(latitude: Double, longitude: Double): List<NearbyStop>
}

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    fun observeIsFavorite(type: FavoriteType, id: String): Flow<Boolean>
    suspend fun toggleFavorite(favorite: Favorite)
}

interface SearchRepository {
    fun observeRecentSearches(): Flow<List<RecentSearch>>
    suspend fun record(search: RecentSearch)
}

interface AnnouncementsRepository {
    fun observeAnnouncements(): Flow<List<Announcement>>
}
