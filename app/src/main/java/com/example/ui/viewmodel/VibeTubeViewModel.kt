package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.VibeTubeRepository
import com.example.ui.components.NavTab
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenState {
    FEED,
    VIDEO_DETAIL,
    SEARCH,
    CHANNEL,
    UPLOAD,
    CREATOR_STUDIO
}

class VibeTubeViewModel(
    private val repository: VibeTubeRepository = VibeTubeRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _screenState = MutableStateFlow(ScreenState.FEED)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private val _selectedVideo = MutableStateFlow<Video?>(null)
    val selectedVideo: StateFlow<Video?> = _selectedVideo.asStateFlow()

    private val _selectedChannelId = MutableStateFlow<String?>(null)
    val selectedChannelId: StateFlow<String?> = _selectedChannelId.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val videos: StateFlow<List<Video>> = repository.videos
    val shorts: StateFlow<List<ShortItem>> = repository.shorts
    val playlists: StateFlow<List<Playlist>> = repository.playlists
    val watchHistory: StateFlow<List<Video>> = repository.watchHistory
    val commentsMap: StateFlow<Map<String, List<Comment>>> = repository.commentsMap
    val channels: StateFlow<Map<String, Channel>> = repository.channels
    val currentUser: StateFlow<User?> = repository.currentUser

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
        if (_screenState.value != ScreenState.FEED && _screenState.value != ScreenState.VIDEO_DETAIL) {
            _screenState.value = ScreenState.FEED
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun playVideo(video: Video) {
        _selectedVideo.value = video
        _screenState.value = ScreenState.VIDEO_DETAIL
        repository.addToHistory(video)
    }

    fun dismissDetailToMiniPlayer() {
        _screenState.value = ScreenState.FEED
    }

    fun closeMiniPlayer() {
        _selectedVideo.value = null
    }

    fun openSearch() {
        _screenState.value = ScreenState.SEARCH
    }

    fun openChannel(channelId: String) {
        _selectedChannelId.value = channelId
        _screenState.value = ScreenState.CHANNEL
    }

    fun openUpload() {
        _screenState.value = ScreenState.UPLOAD
    }

    fun openCreatorStudio() {
        _screenState.value = ScreenState.CREATOR_STUDIO
    }

    fun navigateBack() {
        when (_screenState.value) {
            ScreenState.VIDEO_DETAIL -> _screenState.value = ScreenState.FEED
            ScreenState.SEARCH -> _screenState.value = ScreenState.FEED
            ScreenState.CHANNEL -> _screenState.value = ScreenState.FEED
            ScreenState.UPLOAD -> _screenState.value = ScreenState.FEED
            ScreenState.CREATOR_STUDIO -> _screenState.value = ScreenState.FEED
            ScreenState.FEED -> {
                if (_currentTab.value != NavTab.HOME) {
                    _currentTab.value = NavTab.HOME
                }
            }
        }
    }

    fun toggleLike(videoId: String) {
        val updated = repository.toggleVideoLike(videoId)
        if (_selectedVideo.value?.id == videoId && updated != null) {
            _selectedVideo.value = updated
        }
    }

    fun toggleDislike(videoId: String) {
        val updated = repository.toggleVideoDislike(videoId)
        if (_selectedVideo.value?.id == videoId && updated != null) {
            _selectedVideo.value = updated
        }
    }

    fun toggleSubscription(channelId: String) {
        repository.toggleSubscription(channelId)
        if (_selectedVideo.value?.channelId == channelId) {
            _selectedVideo.value = _selectedVideo.value?.copy(
                isSubscribed = !(_selectedVideo.value?.isSubscribed ?: false)
            )
        }
    }

    fun addComment(videoId: String, text: String) {
        repository.addComment(videoId, text)
    }

    fun toggleShortLike(shortId: String) {
        repository.toggleShortLike(shortId)
    }

    fun toggleShortSubscribe(shortId: String) {
        repository.toggleShortSubscribe(shortId)
    }

    fun createPlaylist(title: String, isPrivate: Boolean) {
        repository.createPlaylist(title, isPrivate)
    }

    fun addVideoToPlaylist(playlistId: String, videoId: String) {
        repository.addVideoToPlaylist(playlistId, videoId)
    }

    fun clearHistory() {
        repository.clearHistory()
    }

    fun publishVideo(
        title: String,
        description: String,
        category: String,
        visibility: VideoVisibility,
        tags: List<String>
    ) {
        val newVid = repository.publishUploadedVideo(title, description, category, visibility, tags)
        _screenState.value = ScreenState.FEED
        _currentTab.value = NavTab.HOME
        _selectedVideo.value = newVid
    }
}
