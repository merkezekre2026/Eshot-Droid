package com.codex.izmireshot.feature.line_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codex.izmireshot.core.model.Direction
import com.codex.izmireshot.feature.map.LeafletMap

@Composable
fun LineDetailScreen(
    onStopClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LineDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val detail = state.detail

    if (detail == null) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("${state.lineNo} hattı yükleniyor...", style = MaterialTheme.typography.titleLarge)
        }
        return
    }

    val route = if (state.direction == Direction.Outbound) detail.outboundShape else detail.inboundShape
    val stops = if (state.direction == Direction.Outbound) detail.outboundStops else detail.inboundStops
    val times = detail.departures.mapNotNull {
        if (state.direction == Direction.Outbound) it.outboundTime else it.inboundTime
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${detail.line.lineNo} ${detail.line.name}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(detail.line.routeDescription.ifBlank { "${detail.line.start} - ${detail.line.end}" })
                Button(onClick = viewModel::toggleFavorite) {
                    Text(if (state.favorite) "Favoriden çıkar" else "Favorilere ekle")
                }
                Button(onClick = viewModel::refreshLive) {
                    Text(if (state.loadingLive) "Canlı veri yenileniyor" else "Resmi canlı konumu yenile")
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Yön", fontWeight = FontWeight.SemiBold)
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = state.direction == Direction.Outbound, onClick = { viewModel.setDirection(Direction.Outbound) }, label = { Text("Gidiş") })
                    FilterChip(selected = state.direction == Direction.Inbound, onClick = { viewModel.setDirection(Direction.Inbound) }, label = { Text("Dönüş") })
                }
            }
        }
        item {
            ElevatedCard(Modifier.fillMaxWidth().height(340.dp)) {
                LeafletMap(
                    routePoints = route,
                    stops = stops,
                    liveBuses = state.liveBuses,
                    userLocation = null,
                    onStopClick = onStopClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        item { Section("Bugünün planlı hareket saatleri") }
        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    text = if (times.isEmpty()) "Bu yön için planlı saat bulunamadı." else times.take(80).joinToString("  "),
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        item { Section("Güzergah durakları") }
        items(stops, key = { it.stopId }) { stop ->
            ListItem(
                headlineContent = { Text(stop.name) },
                supportingContent = { Text("Durak no: ${stop.stopId}") },
                modifier = Modifier.clickable { onStopClick(stop.stopId) },
            )
        }
        if (detail.announcements.isNotEmpty()) {
            item { Section("Duyurular") }
            items(detail.announcements) { announcement ->
                Card(Modifier.fillMaxWidth()) {
                    Text(announcement.title, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Section(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
}
