package com.codex.izmireshot.core.data

import com.codex.izmireshot.core.common.AppDispatchers
import com.codex.izmireshot.core.common.Geo
import com.codex.izmireshot.core.common.TurkishText
import com.codex.izmireshot.core.database.SyncStatusEntity
import com.codex.izmireshot.core.database.TransportDao
import com.codex.izmireshot.core.network.EshotOpenApi
import com.codex.izmireshot.core.network.FileDownloadApi
import com.codex.izmireshot.core.network.OfficialDataSources
import com.codex.izmireshot.core.model.Announcement
import com.codex.izmireshot.core.model.BusLine
import com.codex.izmireshot.core.model.BusStop
import com.codex.izmireshot.core.model.DepartureTime
import com.codex.izmireshot.core.model.Direction
import com.codex.izmireshot.core.model.LineDetail
import com.codex.izmireshot.core.model.LiveBus
import com.codex.izmireshot.core.model.NearbySource
import com.codex.izmireshot.core.model.NearbyStop
import com.codex.izmireshot.core.model.RoutePoint
import com.codex.izmireshot.core.model.StopDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.content
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransportRepositoryImpl @Inject constructor(
    private val transportDao: TransportDao,
    private val fileApi: FileDownloadApi,
    private val openApi: EshotOpenApi,
    private val dispatchers: AppDispatchers,
) : TransportRepository, NearbyRepository, AnnouncementsRepository {
    private val csv = CsvParser()

    override fun searchLines(query: String): Flow<List<BusLine>> =
        transportDao.searchLines(TurkishText.normalizeForSearch(query)).map { rows -> rows.map { it.toModel() } }

    override fun searchStops(query: String): Flow<List<BusStop>> =
        transportDao.searchStops(TurkishText.normalizeForSearch(query)).map { rows -> rows.map { it.toModel() } }

    override fun observeLineDetail(lineNo: Int): Flow<LineDetail?> = combine(
        transportDao.observeLine(lineNo),
        transportDao.observeDepartures(lineNo),
        transportDao.observeRoute(lineNo, Direction.Outbound.id),
        transportDao.observeRoute(lineNo, Direction.Inbound.id),
        transportDao.observeAnnouncements(lineNo),
    ) { line, departures, outbound, inbound, announcements ->
        line?.let {
            PartialLineDetail(
                line = it.toModel(),
                departures = departures.map { item -> item.toModel() },
                outboundShape = outbound.map { item -> item.toModel() },
                inboundShape = inbound.map { item -> item.toModel() },
                announcements = announcements.map { item -> item.toModel() },
            )
        }
    }.combine(transportDao.observeStopsServingLine(lineNo)) { partial, stops ->
        partial?.let {
            val routeStops = stops.map { item -> item.toModel() }
            LineDetail(
                line = it.line,
                departures = it.departures,
                outboundStops = routeStops,
                inboundStops = routeStops,
                outboundShape = it.outboundShape,
                inboundShape = it.inboundShape,
                announcements = it.announcements,
                liveBuses = emptyList(),
            )
        }
    }

    override fun observeStopDetail(stopId: Int): Flow<StopDetail?> =
        transportDao.observeStop(stopId).map { it?.toModel() }.combineLines()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<BusStop?>.combineLines(): Flow<StopDetail?> = flatMapLatest { stop ->
        if (stop == null) {
            flowOf(null)
        } else if (stop.servingLines.isEmpty()) {
            flowOf(StopDetail(stop, emptyList(), emptyList()))
        } else {
            transportDao.observeLinesByIds(stop.servingLines).map { lines ->
                StopDetail(stop, lines.map { it.toModel() }, emptyList())
            }
        }
    }

    override fun observeRoute(lineNo: Int, directionId: Int) =
        transportDao.observeRoute(lineNo, directionId).map { rows -> rows.map { it.toModel() } }

    override suspend fun refreshAll(): Unit = withContext(dispatchers.io) {
        coroutineScope {
            listOf(
                async { syncLines() },
                async { syncStops() },
                async { syncRoutes() },
                async { syncDepartures() },
                async { syncAnnouncements() },
                async { syncConnections() },
            ).awaitAll()
        }
    }

    override suspend fun refreshLiveBuses(lineNo: Int): List<LiveBus> = withContext(dispatchers.io) {
        runCatching { parseLiveBuses(openApi.liveBusLocations(lineNo), lineNo) }.getOrDefault(emptyList())
    }

    override suspend fun refreshApproachingBuses(stopId: Int): List<LiveBus> = withContext(dispatchers.io) {
        runCatching { parseLiveBuses(openApi.approachingBuses(stopId), null) }.getOrDefault(emptyList())
    }

    override suspend fun nearbyStops(latitude: Double, longitude: Double): List<NearbyStop> = withContext(dispatchers.io) {
        val official = runCatching {
            parseNearbyStops(openApi.nearbyStops(longitude, latitude))
        }.getOrDefault(emptyList())
        official.ifEmpty {
            transportDao.getAllStops()
                .asSequence()
                .map { it.toModel() }
                .map { stop -> NearbyStop(stop, Geo.distanceMeters(latitude, longitude, stop.latitude, stop.longitude), NearbySource.LocalCache) }
                .sortedBy { it.distanceMeters }
                .take(50)
                .toList()
        }
    }

    override fun observeAnnouncements() =
        transportDao.observeAllAnnouncements().map { rows -> rows.map { it.toModel() } }

    private suspend fun syncLines() = sync("lines") {
        val models = OfficialCsvAdapters.lines(downloadCsv(OfficialDataSources.LINES_CSV))
        transportDao.upsertLines(models.map { it.toEntity() })
    }

    private suspend fun syncStops() = sync("stops") {
        val models = OfficialCsvAdapters.stops(downloadCsv(OfficialDataSources.STOPS_CSV))
        transportDao.upsertStops(models.map { it.toEntity() })
    }

    private suspend fun syncRoutes() = sync("routes") {
        val models = OfficialCsvAdapters.routes(downloadCsv(OfficialDataSources.ROUTES_CSV))
        transportDao.upsertRoutePoints(models.map { it.toEntity() })
    }

    private suspend fun syncDepartures() = sync("departures") {
        val models = OfficialCsvAdapters.departures(downloadCsv(OfficialDataSources.DEPARTURES_CSV))
        transportDao.upsertDepartures(models.map { it.toEntity() })
    }

    private suspend fun syncAnnouncements() = sync("announcements") {
        val models = OfficialCsvAdapters.announcements(downloadCsv(OfficialDataSources.ANNOUNCEMENTS_CSV))
        transportDao.upsertAnnouncements(models.map { it.toEntity() })
    }

    private suspend fun syncConnections() = sync("connections") {
        val models = OfficialCsvAdapters.connections(downloadCsv(OfficialDataSources.CONNECTIONS_CSV))
        transportDao.upsertConnections(models.map { it.toEntity() })
    }

    private suspend fun downloadCsv(url: String) = csv.parse(fileApi.download(url).bytes().toString(Charsets.UTF_8))

    private suspend fun sync(source: String, block: suspend () -> Unit) {
        runCatching { block() }
            .onSuccess { transportDao.upsertSyncStatus(SyncStatusEntity(source, System.currentTimeMillis(), null)) }
            .onFailure { transportDao.upsertSyncStatus(SyncStatusEntity(source, System.currentTimeMillis(), it.message)) }
    }

    private fun parseNearbyStops(json: JsonElement): List<NearbyStop> {
        val items = json.items()
        return items.mapNotNull { item ->
            val obj = item.jsonObject
            val stopId = obj.findInt("DURAK_ID", "durakId", "id") ?: return@mapNotNull null
            val name = obj.findString("DURAK_ADI", "durakAdi", "adi", "name").orEmpty()
            val lat = obj.findDouble("ENLEM", "enlem", "lat", "y") ?: return@mapNotNull null
            val lon = obj.findDouble("BOYLAM", "boylam", "lon", "lng", "x") ?: return@mapNotNull null
            val distance = obj.findDouble("MESAFE", "mesafe", "distance") ?: 0.0
            NearbyStop(BusStop(stopId, name, lat, lon, emptyList()), distance, NearbySource.OfficialApi)
        }.sortedBy { it.distanceMeters }
    }

    private fun parseLiveBuses(json: JsonElement, fallbackLineNo: Int?): List<LiveBus> = json.items().mapNotNull { item ->
        val obj = item.jsonObject
        val lat = obj.findDouble("ENLEM", "enlem", "lat", "y") ?: return@mapNotNull null
        val lon = obj.findDouble("BOYLAM", "boylam", "lon", "lng", "x") ?: return@mapNotNull null
        LiveBus(
            lineNo = obj.findInt("HAT_NO", "hatNo", "hatNumarasi") ?: fallbackLineNo,
            vehicleId = obj.findString("ARAC_NO", "aracNo", "plaka", "vehicleId"),
            latitude = lat,
            longitude = lon,
            speed = obj.findDouble("HIZ", "hiz", "speed"),
            lastUpdated = obj.findString("SON_GUNCELLEME", "sonGuncelleme", "lastUpdated"),
        )
    }

    private fun JsonElement.items(): List<JsonElement> = when (this) {
        is JsonArray -> this
        is JsonObject -> {
            val candidates = listOf("data", "Data", "items", "Items", "features", "Features")
            candidates.firstNotNullOfOrNull { key -> this[key] as? JsonArray } ?: listOf(this)
        }
        else -> emptyList()
    }

    private fun JsonObject.findString(vararg keys: String): String? =
        keys.firstNotNullOfOrNull { key -> this[key]?.jsonPrimitive?.content }

    private fun JsonObject.findInt(vararg keys: String): Int? =
        keys.firstNotNullOfOrNull { key -> this[key]?.jsonPrimitive?.intOrNull ?: this[key]?.jsonPrimitive?.content?.toIntOrNull() }

    private fun JsonObject.findDouble(vararg keys: String): Double? =
        keys.firstNotNullOfOrNull { key -> this[key]?.jsonPrimitive?.doubleOrNull ?: this[key]?.jsonPrimitive?.content?.replace(',', '.')?.toDoubleOrNull() }
}

private data class PartialLineDetail(
    val line: BusLine,
    val departures: List<DepartureTime>,
    val outboundShape: List<RoutePoint>,
    val inboundShape: List<RoutePoint>,
    val announcements: List<Announcement>,
)
