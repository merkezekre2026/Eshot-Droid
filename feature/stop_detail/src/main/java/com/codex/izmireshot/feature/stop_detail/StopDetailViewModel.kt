package com.codex.izmireshot.feature.stop_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.izmireshot.core.data.FavoritesRepository
import com.codex.izmireshot.core.data.TransportRepository
import com.codex.izmireshot.core.data.favoriteForStop
import com.codex.izmireshot.core.model.FavoriteType
import com.codex.izmireshot.core.model.LiveBus
import com.codex.izmireshot.core.model.StopDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StopDetailUiState(
    val stopId: Int,
    val detail: StopDetail? = null,
    val approaching: List<LiveBus> = emptyList(),
    val favorite: Boolean = false,
    val loadingLive: Boolean = false,
)

@HiltViewModel
class StopDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transportRepository: TransportRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {
    private val stopId: Int = checkNotNull(savedStateHandle["stopId"])
    private val approaching = MutableStateFlow<List<LiveBus>>(emptyList())
    private val loadingLive = MutableStateFlow(false)

    val uiState: StateFlow<StopDetailUiState> = combine(
        transportRepository.observeStopDetail(stopId),
        approaching,
        favoritesRepository.observeIsFavorite(FavoriteType.Stop, stopId.toString()),
        loadingLive,
    ) { detail, buses, favorite, liveLoading ->
        StopDetailUiState(stopId, detail, buses, favorite, liveLoading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StopDetailUiState(stopId))

    init {
        refreshLive()
    }

    fun refreshLive() {
        viewModelScope.launch {
            loadingLive.value = true
            approaching.value = transportRepository.refreshApproachingBuses(stopId)
            loadingLive.value = false
        }
    }

    fun toggleFavorite() {
        uiState.value.detail?.stop?.let { stop ->
            viewModelScope.launch { favoritesRepository.toggleFavorite(favoriteForStop(stop)) }
        }
    }
}
