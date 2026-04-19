package com.codex.izmireshot.feature.nearby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt

@Composable
fun NearbyScreen(
    onStopClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NearbyViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            context.lastKnownLocation()?.let { viewModel.load(it.first, it.second) }
                ?: viewModel.permissionDenied()
        } else {
            viewModel.permissionDenied()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yakınımdaki Duraklar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Konumunuz yalnızca en yakın resmi ESHOT duraklarını sıralamak için kullanılır.")
                Button(onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        context.lastKnownLocation()?.let { viewModel.load(it.first, it.second) }
                    } else {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }) { Text("Konumumla ara") }
                if (state.loading) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                if (state.message.isNotBlank()) Text(state.message)
            }
        }
        items(state.stops, key = { it.stop.stopId }) { nearby ->
            ListItem(
                headlineContent = { Text(nearby.stop.name) },
                supportingContent = { Text("${nearby.distanceMeters.roundToInt()} m • ${nearby.source}") },
                modifier = Modifier.clickable { onStopClick(nearby.stop.stopId) },
            )
        }
    }
}

private fun Context.lastKnownLocation(): Pair<Double, Double>? {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
    return providers.firstNotNullOfOrNull { provider ->
        runCatching {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                manager.getLastKnownLocation(provider)?.let { it.latitude to it.longitude }
            } else {
                null
            }
        }.getOrNull()
    }
}
