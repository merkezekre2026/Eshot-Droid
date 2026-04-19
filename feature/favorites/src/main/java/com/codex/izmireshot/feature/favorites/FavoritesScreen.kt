package com.codex.izmireshot.feature.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.codex.izmireshot.core.model.FavoriteType

@Composable
fun FavoritesScreen(
    onLineClick: (Int) -> Unit,
    onStopClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsState()
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp)) {
        item {
            Text("Favoriler", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        }
        if (favorites.isEmpty()) {
            item { Text("Henüz favori hat veya durak yok.") }
        }
        items(favorites, key = { "${it.type}-${it.id}" }) { favorite ->
            ListItem(
                headlineContent = { Text(favorite.title) },
                supportingContent = { Text(favorite.subtitle) },
                modifier = Modifier.clickable {
                    if (favorite.type == FavoriteType.Line) onLineClick(favorite.id.toInt()) else onStopClick(favorite.id.toInt())
                },
            )
        }
    }
}
