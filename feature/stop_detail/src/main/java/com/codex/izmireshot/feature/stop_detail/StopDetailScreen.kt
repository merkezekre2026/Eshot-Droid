package com.codex.izmireshot.feature.stop_detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StopDetailScreen(
    onLineClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StopDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val detail = state.detail
    if (detail == null) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("${state.stopId} durağı yükleniyor...", style = MaterialTheme.typography.titleLarge)
        }
        return
    }
    val stop = detail.stop
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stop.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Durak no: ${stop.stopId}")
                Text("Konum: ${stop.latitude}, ${stop.longitude}")
                Button(onClick = viewModel::toggleFavorite) {
                    Text(if (state.favorite) "Favoriden çıkar" else "Favorilere ekle")
                }
                Button(onClick = {
                    val uri = Uri.parse("geo:${stop.latitude},${stop.longitude}?q=${stop.latitude},${stop.longitude}(${Uri.encode(stop.name)})")
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }) { Text("Haritalarda aç") }
                Button(onClick = viewModel::refreshLive) {
                    Text(if (state.loadingLive) "Canlı veri yenileniyor" else "Yaklaşan otobüsleri yenile")
                }
            }
        }
        item { Section("Yaklaşan otobüsler") }
        if (state.approaching.isEmpty()) {
            item { InfoCard("Resmi canlı yaklaşan otobüs verisi bulunamadı. Sahte tahmin gösterilmiyor.") }
        } else {
            items(state.approaching) { bus ->
                ListItem(
                    headlineContent = { Text("Hat ${bus.lineNo ?: "-"}") },
                    supportingContent = { Text(bus.lastUpdated ?: "Resmi canlı veri") },
                )
            }
        }
        item { Section("Bu duraktan geçen hatlar") }
        items(detail.servingLines, key = { it.lineNo }) { line ->
            ListItem(
                headlineContent = { Text("${line.lineNo} ${line.name}") },
                supportingContent = { Text(line.routeDescription) },
                modifier = Modifier.clickable { onLineClick(line.lineNo) },
            )
        }
    }
}

@Composable
private fun Section(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun InfoCard(text: String) {
    Card(Modifier.fillMaxWidth()) { Text(text, Modifier.padding(16.dp)) }
}
