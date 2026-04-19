package com.codex.izmireshot.feature.line_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.izmireshot.core.data.FavoritesRepository
import com.codex.izmireshot.core.data.TransportRepository
import com.codex.izmireshot.core.data.favoriteForLine
import com.codex.izmireshot.core.model.Direction
import com.codex.izmireshot.core.model.FavoriteType
import com.codex.izmireshot.core.model.LineDetail
import com.codex.izmireshot.core.model.LiveBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LineDetailUiState(
    val lineNo: Int,
    val detail: LineDetail? = null,
    val direction: Direction = Direction.Outbound,
    val liveBuses: List<LiveBus> = emptyList(),
    val favorite: Boolean = false,
    val loadingLive: Boolean = false,
)

@HiltViewModel
class LineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transportRepository: TransportRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {
    private val lineNo: Int = checkNotNull(savedStateHandle["lineNo"])
    private val direction = MutableStateFlow(Direction.Outbound)
    private val liveBuses = MutableStateFlow<List<LiveBus>>(emptyList())
    private val loadingLive = MutableStateFlow(false)

    val uiState: StateFlow<LineDetailUiState> = combine(
        transportRepository.observeLineDetail(lineNo),
        direction,
        liveBuses,
        favoritesRepository.observeIsFavorite(FavoriteType.Line, lineNo.toString()),
        loadingLive,
    ) { detail, dir, buses, favorite, liveLoading ->
        LineDetailUiState(lineNo, detail, dir, buses, favorite, liveLoading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LineDetailUiState(lineNo))

    init {
        refreshLive()
    }

    fun setDirection(value: Direction) {
        direction.value = value
    }

    fun refreshLive() {
        viewModelScope.launch {
            loadingLive.value = true
            liveBuses.value = transportRepository.refreshLiveBuses(lineNo)
            loadingLive.value = false
        }
    }

    fun toggleFavorite() {
        uiState.value.detail?.line?.let { line ->
            viewModelScope.launch { favoritesRepository.toggleFavorite(favoriteForLine(line)) }
        }
    }
}
