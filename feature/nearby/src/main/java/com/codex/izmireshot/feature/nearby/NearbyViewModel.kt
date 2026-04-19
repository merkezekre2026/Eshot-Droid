package com.codex.izmireshot.feature.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.izmireshot.core.data.NearbyRepository
import com.codex.izmireshot.core.model.NearbyStop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NearbyUiState(
    val loading: Boolean = false,
    val stops: List<NearbyStop> = emptyList(),
    val message: String = "Yakındaki durakları görmek için konum izni gerekir.",
)

@HiltViewModel
class NearbyViewModel @Inject constructor(
    private val repository: NearbyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NearbyUiState())
    val uiState: StateFlow<NearbyUiState> = _uiState

    fun load(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = "Yakındaki duraklar aranıyor...")
            val stops = runCatching { repository.nearbyStops(latitude, longitude) }.getOrDefault(emptyList())
            _uiState.value = NearbyUiState(
                loading = false,
                stops = stops,
                message = if (stops.isEmpty()) "Yakında durak bulunamadı veya veri alınamadı." else "",
            )
        }
    }

    fun permissionDenied() {
        _uiState.value = _uiState.value.copy(message = "Konum izni verilmedi. Uygulamanın diğer bölümleri çalışmaya devam eder.")
    }
}
