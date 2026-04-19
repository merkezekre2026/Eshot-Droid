package com.codex.izmireshot.feature.announcements

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnnouncementsScreen(
    modifier: Modifier = Modifier,
    viewModel: AnnouncementsViewModel = hiltViewModel(),
) {
    val announcements by viewModel.announcements.collectAsState()
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Text("Duyurular", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        }
        if (announcements.isEmpty()) {
            item { Text("Resmi duyuru verisi bulunamadı.") }
        }
        items(announcements) { announcement ->
            Card(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Text("Hat ${announcement.lineNo}", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(16.dp, 12.dp, 16.dp, 0.dp))
                Text(announcement.title, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
