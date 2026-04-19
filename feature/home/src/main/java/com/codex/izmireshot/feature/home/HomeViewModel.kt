package com.codex.izmireshot.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.izmireshot.core.data.SearchRepository
import com.codex.izmireshot.core.data.TransportRepository
import com.codex.izmireshot.core.model.BusLine
import com.codex.izmireshot.core.model.BusStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val query: String = "",
    val lines: List<BusLine> = emptyList(),
    val stops: List<BusStop> = emptyList(),
    val syncing: Boolean = false,
    val error: String? = null,
)

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transportRepository: TransportRepository,
    searchRepository: SearchRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val syncing = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    private val results = query.debounce(250).flatMapLatest { value ->
        if (value.isBlank()) {
            combine(
                transportRepository.searchLines(""),
                transportRepository.searchStops(""),
            ) { lines, stops -> lines.take(20) to stops.take(20) }
        } else {
            combine(
                transportRepository.searchLines(value),
                transportRepository.searchStops(value),
            ) { lines, stops -> lines to stops }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(query, syncing, error, results) { q, s, e, r ->
        HomeUiState(query = q, syncing = s, error = e, lines = r.first, stops = r.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    val recentSearches = searchRepository.observeRecentSearches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun refresh() {
        viewModelScope.launch {
            syncing.value = true
            error.value = null
            runCatching { transportRepository.refreshAll() }
                .onFailure { error.value = "Veriler yenilenemedi. Önbellekteki bilgiler gösteriliyor." }
            syncing.value = false
        }
    }
}
