package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.Video
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.VibeRed
import com.example.ui.theme.VibeTubeTheme
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.VibeTubeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VibeTubeTheme {
                VibeTubeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibeTubeApp(viewModel: VibeTubeViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
    val selectedChannelId by viewModel.selectedChannelId.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val shorts by viewModel.shorts.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val commentsMap by viewModel.commentsMap.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var showBackendConfigDialog by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    // Intercept hardware/system back button
    BackHandler(enabled = screenState != ScreenState.FEED || currentTab != NavTab.HOME) {
        viewModel.navigateBack()
    }

    val isFullscreenPlaying = screenState == ScreenState.VIDEO_DETAIL && selectedVideo != null
    val isShortsActive = currentTab == NavTab.SHORTS && screenState == ScreenState.FEED

    Scaffold(
        topBar = {
            if (!isFullscreenPlaying && !isShortsActive && screenState == ScreenState.FEED) {
                VibeTopBar(
                    currentUser = currentUser,
                    onSearchClick = { viewModel.openSearch() },
                    onNotificationsClick = { showNotificationsSheet = true },
                    onProfileClick = { showBackendConfigDialog = true }
                )
            }
        },
        bottomBar = {
            if (!isFullscreenPlaying) {
                Column {
                    // Mini player docked above bottom bar when active
                    if (selectedVideo != null && screenState == ScreenState.FEED && !isShortsActive) {
                        MiniPlayerBar(
                            video = selectedVideo!!,
                            isPlaying = true,
                            onPlayPauseToggle = { /* toggle playback */ },
                            onExpand = { viewModel.playVideo(selectedVideo!!) },
                            onClose = { viewModel.closeMiniPlayer() }
                        )
                    }

                    VibeBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        onUploadClick = { viewModel.openUpload() }
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize().testTag("vibetube_root_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (screenState) {
                ScreenState.VIDEO_DETAIL -> {
                    selectedVideo?.let { vid ->
                        VideoDetailScreen(
                            video = vid,
                            relatedVideos = videos,
                            comments = commentsMap[vid.id] ?: emptyList(),
                            onBack = { viewModel.dismissDetailToMiniPlayer() },
                            onLikeToggle = { viewModel.toggleLike(vid.id) },
                            onDislikeToggle = { viewModel.toggleDislike(vid.id) },
                            onSubscribeToggle = { viewModel.toggleSubscription(vid.channelId) },
                            onChannelClick = { chId -> viewModel.openChannel(chId) },
                            onRelatedVideoClick = { rel -> viewModel.playVideo(rel) },
                            onAddComment = { text -> viewModel.addComment(vid.id, text) },
                            onSaveToPlaylist = { plVideo ->
                                playlists.firstOrNull()?.let { pl ->
                                    viewModel.addVideoToPlaylist(pl.id, plVideo.id)
                                }
                            }
                        )
                    }
                }

                ScreenState.SEARCH -> {
                    SearchScreen(
                        videos = videos,
                        onBack = { viewModel.navigateBack() },
                        onVideoClick = { vid -> viewModel.playVideo(vid) },
                        onChannelClick = { chId -> viewModel.openChannel(chId) }
                    )
                }

                ScreenState.CHANNEL -> {
                    val channel = channels[selectedChannelId] ?: channels.values.firstOrNull()
                    if (channel != null) {
                        ChannelScreen(
                            channel = channel,
                            videos = videos,
                            onBack = { viewModel.navigateBack() },
                            onVideoClick = { vid -> viewModel.playVideo(vid) },
                            onSubscribeToggle = { chId -> viewModel.toggleSubscription(chId) }
                        )
                    }
                }

                ScreenState.UPLOAD -> {
                    UploadScreen(
                        onBack = { viewModel.navigateBack() },
                        onPublishSuccess = { title, desc, cat, vis, tags ->
                            viewModel.publishVideo(title, desc, cat, vis, tags)
                        }
                    )
                }

                ScreenState.CREATOR_STUDIO -> {
                    CreatorStudioScreen(
                        videos = videos,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                ScreenState.FEED -> {
                    when (currentTab) {
                        NavTab.HOME -> {
                            HomeScreen(
                                videos = videos,
                                shorts = shorts,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onVideoClick = { vid -> viewModel.playVideo(vid) },
                                onShortClick = { viewModel.selectTab(NavTab.SHORTS) },
                                onChannelClick = { chId -> viewModel.openChannel(chId) },
                                onSaveToWatchLater = { vid ->
                                    viewModel.addVideoToPlaylist("pl_watch_later", vid.id)
                                },
                                onSaveToPlaylist = { vid ->
                                    playlists.firstOrNull()?.let { pl ->
                                        viewModel.addVideoToPlaylist(pl.id, vid.id)
                                    }
                                }
                            )
                        }

                        NavTab.SHORTS -> {
                            ShortsScreen(
                                shorts = shorts,
                                comments = commentsMap.values.flatten().take(5),
                                onLikeToggle = { sId -> viewModel.toggleShortLike(sId) },
                                onSubscribeToggle = { sId -> viewModel.toggleShortSubscribe(sId) },
                                onAddComment = { sId, text ->
                                    viewModel.addComment("vid_1", text)
                                }
                            )
                        }

                        NavTab.SUBSCRIPTIONS -> {
                            SubscriptionsScreen(
                                channels = channels.values.toList(),
                                videos = videos.filter { it.isSubscribed },
                                onVideoClick = { vid -> viewModel.playVideo(vid) },
                                onChannelClick = { chId -> viewModel.openChannel(chId) }
                            )
                        }

                        NavTab.LIBRARY -> {
                            LibraryScreen(
                                currentUser = currentUser,
                                history = watchHistory,
                                playlists = playlists,
                                onVideoClick = { vid -> viewModel.playVideo(vid) },
                                onClearHistory = { viewModel.clearHistory() },
                                onCreatePlaylist = { title, isPriv ->
                                    viewModel.createPlaylist(title, isPriv)
                                },
                                onOpenCreatorStudio = { viewModel.openCreatorStudio() },
                                onOpenBackendConfig = { showBackendConfigDialog = true }
                            )
                        }
                    }
                }
            }
        }
    }

    // Backend Config Dialog
    if (showBackendConfigDialog) {
        BackendConfigDialog(
            onDismiss = { showBackendConfigDialog = false }
        )
    }

    // Notifications Bottom Sheet
    if (showNotificationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationsSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                listOf(
                    "TechPulse Studio uploaded: Building a Distributed Video Streaming Pipeline" to "2 hours ago",
                    "FullStack Forge replied to your comment on StateFlow architecture" to "5 hours ago",
                    "Your video finished transcoding into 1080p, 720p, 480p HLS renditions" to "1 day ago"
                ).forEach { (notif, time) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VibeRed.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = VibeRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = notif,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = time,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionsScreen(
    channels: List<com.example.data.model.Channel>,
    videos: List<Video>,
    onVideoClick: (Video) -> Unit,
    onChannelClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Channel Stories / Avatars Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(channels) { ch ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onChannelClick(ch.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = ch.avatarUrl,
                            contentDescription = ch.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ch.name.split(" ").firstOrNull() ?: ch.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        // Subscriptions Feed
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(videos, key = { it.id }) { video ->
                VideoCard(
                    video = video,
                    onVideoClick = onVideoClick,
                    onChannelClick = onChannelClick,
                    onSaveToWatchLater = {},
                    onSaveToPlaylist = {}
                )
            }
        }
    }
}
