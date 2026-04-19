package com.codex.izmireshot.feature.announcements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codex.izmireshot.core.data.AnnouncementsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnnouncementsViewModel @Inject constructor(
    repository: AnnouncementsRepository,
) : ViewModel() {
    val announcements = repository.observeAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
