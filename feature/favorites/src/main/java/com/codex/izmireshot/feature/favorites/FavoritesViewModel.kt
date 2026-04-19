package com.codex.izmireshot.feature.favorites

import androidx.lifecycle.ViewModel
import com.codex.izmireshot.core.data.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    repository: FavoritesRepository,
) : ViewModel() {
    val favorites = repository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
