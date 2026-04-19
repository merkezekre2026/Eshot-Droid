package com.codex.izmireshot.core.network

import kotlinx.serialization.json.JsonElement
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface FileDownloadApi {
    @GET
    suspend fun download(@Url url: String): ResponseBody
}

interface EshotOpenApi {
    @GET("api/ibb/cbs/noktayayakinduraklar")
    suspend fun nearbyStops(
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("inCoordSys") inCoordSys: String = "EPSG:4326",
        @Query("outCoordSys") outCoordSys: String = "EPSG:4326",
    ): JsonElement

    @GET("api/iztek/hatotobuskonumlari/{hatNo}")
    suspend fun liveBusLocations(@Path("hatNo") lineNo: Int): JsonElement

    @GET("api/iztek/duragayaklasanotobusler/{stopId}")
    suspend fun approachingBuses(@Path("stopId") stopId: Int): JsonElement

    @GET("api/iztek/hatduyurulari/{languageId}/{lineId}")
    suspend fun lineAnnouncements(
        @Path("languageId") languageId: Int = 1,
        @Path("lineId") lineId: Int,
    ): JsonElement
}
