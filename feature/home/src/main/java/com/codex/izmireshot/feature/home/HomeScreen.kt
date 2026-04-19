package com.codex.izmireshot.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    onLineClick: (Int) -> Unit,
    onStopClick: (Int) -> Unit,
    onNearbyClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = state.syncing,
        onRefresh = viewModel::refresh,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("İzmir ESHOT", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Hat, durak, güzergah ve planlı hareket saatlerini resmi kaynaklardan keşfedin.")
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::onQueryChange,
                        label = { Text("Hat no, hat adı, durak adı veya durak no") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = onNearbyClick, label = { Text("Yakınımdaki Duraklar") })
                        AssistChip(onClick = onFavoritesClick, label = { Text("Favoriler") })
                        AssistChip(onClick = onAnnouncementsClick, label = { Text("Duyurular") })
                    }
                    if (state.syncing) Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                        Text("Resmi veriler eşitleniyor...")
                    }
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
            item { SectionTitle("Hatlar") }
            if (state.lines.isEmpty()) item { EmptyCard("Hat bulunamadı") }
            items(state.lines, key = { "line-${it.lineNo}" }) { line ->
                ListItem(
                    headlineContent = { Text("${line.lineNo} ${line.name}") },
                    supportingContent = { Text(line.routeDescription.ifBlank { "${line.start} - ${line.end}" }) },
                    modifier = Modifier.clickable { onLineClick(line.lineNo) },
                )
            }
            item { Spacer(Modifier.height(8.dp)); SectionTitle("Duraklar") }
            if (state.stops.isEmpty()) item { EmptyCard("Durak bulunamadı") }
            items(state.stops, key = { "stop-${it.stopId}" }) { stop ->
                ListItem(
                    headlineContent = { Text(stop.name) },
                    supportingContent = { Text("Durak no: ${stop.stopId} • Hatlar: ${stop.servingLines.take(8).joinToString(", ")}") },
                    modifier = Modifier.clickable { onStopClick(stop.stopId) },
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun EmptyCard(text: String) {
    Card(Modifier.fillMaxWidth()) {
        Text(text, modifier = Modifier.padding(18.dp))
    }
}
