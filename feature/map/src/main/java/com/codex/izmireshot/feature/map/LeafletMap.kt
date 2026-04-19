package com.codex.izmireshot.feature.map

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.codex.izmireshot.core.model.BusStop
import com.codex.izmireshot.core.model.LiveBus
import com.codex.izmireshot.core.model.RoutePoint
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LeafletMap(
    routePoints: List<RoutePoint>,
    stops: List<BusStop>,
    liveBuses: List<LiveBus>,
    userLocation: Pair<Double, Double>?,
    onStopClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bridge = remember { LeafletBridge(onStopClick) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                webViewClient = WebViewClient()
                addJavascriptInterface(bridge, "AndroidBridge")
                loadUrl("file:///android_asset/map/map.html")
            }
        },
        update = { webView ->
            webView.post {
                webView.evaluateJavascript(
                    "window.renderTransit(${routePointsToJson(routePoints)}, ${stopsToJson(stops)}, ${liveBusesToJson(liveBuses)}, ${userLocation.toJson()});",
                    null,
                )
            }
        },
    )

    LaunchedEffect(onStopClick) {
        bridge.onStopClick = onStopClick
    }
}

class LeafletBridge(
    var onStopClick: (Int) -> Unit,
) {
    @JavascriptInterface
    fun stopSelected(stopId: String) {
        stopId.toIntOrNull()?.let(onStopClick)
    }
}

private fun routePointsToJson(points: List<RoutePoint>): String = JSONArray().also { array ->
    points.forEach { point ->
        array.put(JSONObject().put("lat", point.latitude).put("lon", point.longitude))
    }
}.toString()

private fun stopsToJson(stops: List<BusStop>): String = JSONArray().also { array ->
    stops.forEach { stop ->
        array.put(
            JSONObject()
                .put("id", stop.stopId)
                .put("name", stop.name)
                .put("lat", stop.latitude)
                .put("lon", stop.longitude),
        )
    }
}.toString()

private fun liveBusesToJson(buses: List<LiveBus>): String = JSONArray().also { array ->
    buses.forEach { bus ->
        array.put(
            JSONObject()
                .put("id", bus.vehicleId ?: "")
                .put("lineNo", bus.lineNo ?: "")
                .put("lat", bus.latitude)
                .put("lon", bus.longitude),
        )
    }
}.toString()

private fun Pair<Double, Double>?.toJson(): String =
    this?.let { JSONObject().put("lat", first).put("lon", second).toString() } ?: "null"
