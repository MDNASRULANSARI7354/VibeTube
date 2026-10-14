package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Playlist
import com.example.data.model.User
import com.example.data.model.Video
import com.example.ui.theme.VibeRed

@Composable
fun LibraryScreen(
    currentUser: User?,
    history: List<Video>,
    playlists: List<Playlist>,
    onVideoClick: (Video) -> Unit,
    onClearHistory: () -> Unit,
    onCreatePlaylist: (String, Boolean) -> Unit,
    onOpenCreatorStudio: () -> Unit,
    onOpenBackendConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showNewPlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistTitle by remember { mutableStateOf("") }
    var isPlaylistPrivate by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // User Profile Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (currentUser?.avatarUrl != null) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = currentUser.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp).align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentUser?.name ?: "Guest User",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentUser?.handle ?: "@guest",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onOpenBackendConfig,
                    modifier = Modifier.testTag("library_backend_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Backend Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Text("Clear history", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }
        }

        if (history.isEmpty()) {
            Text(
                text = "No watch history yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(history, key = { it.id }) { video ->
                    HistoryCard(video = video, onClick = { onVideoClick(video) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        // Playlists Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Playlists",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = { showNewPlaylistDialog = true },
                modifier = Modifier.testTag("new_playlist_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New playlist", fontSize = 12.sp)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            playlists.forEach { pl ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* open playlist */ }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (pl.id == "pl_liked") Icons.Filled.ThumbUp else Icons.Filled.PlaylistPlay,
                                contentDescription = null,
                                tint = VibeRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pl.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${pl.videoCount} videos • ${if (pl.isPrivate) "Private" else "Public"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(16.dp))

        // Creator Studio Entry Button
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onOpenCreatorStudio() }
                .testTag("library_creator_studio_btn")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = VibeRed,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VibeTube Creator Studio",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time analytics, revenue, and channel metrics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // New Playlist Dialog
    if (showNewPlaylistDialog) {
        AlertDialog(
            onDismissRequest = { showNewPlaylistDialog = false },
            title = { Text("New Playlist") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newPlaylistTitle,
                        onValueChange = { newPlaylistTitle = it },
                        label = { Text("Title") },
                        placeholder = { Text("Enter playlist title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_playlist_title_input")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isPlaylistPrivate,
                            onCheckedChange = { isPlaylistPrivate = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Keep playlist private")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistTitle.isNotBlank()) {
                            onCreatePlaylist(newPlaylistTitle, isPlaylistPrivate)
                            newPlaylistTitle = ""
                            showNewPlaylistDialog = false
                        }
                    },
                    modifier = Modifier.testTag("create_playlist_confirm_btn")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPlaylistDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryCard(
    video: Video,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                Text(
                    text = video.durationFormatted,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = video.title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = video.channelTitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
