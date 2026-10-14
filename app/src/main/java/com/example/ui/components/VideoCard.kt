package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Video

@Composable
fun VideoCard(
    video: Video,
    onVideoClick: (Video) -> Unit,
    onChannelClick: (String) -> Unit,
    onSaveToWatchLater: (Video) -> Unit,
    onSaveToPlaylist: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onVideoClick(video) }
            .padding(bottom = 16.dp)
            .testTag("video_card_${video.id}")
    ) {
        // 16:9 Thumbnail Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Duration Pill Badge
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.durationFormatted,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Details Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onChannelClick(video.channelId) }
                    .testTag("video_card_channel_avatar_${video.id}"),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = video.channelAvatarUrl,
                    contentDescription = video.channelTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Metadata Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.channelTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (video.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Channel",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    Text(
                        text = " • ${formatViews(video.viewsCount)} • ${video.publishedAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3-dots Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp).testTag("video_card_menu_${video.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Save to Watch Later") },
                        leadingIcon = { Icon(Icons.Outlined.WatchLater, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onSaveToWatchLater(video)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Save to Playlist") },
                        leadingIcon = { Icon(Icons.Outlined.PlaylistAdd, contentDescription = null) },
                        onClick = {
                            showMenu = false
                            onSaveToPlaylist(video)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Share") },
                        leadingIcon = { Icon(Icons.Outlined.Share, contentDescription = null) },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Not interested") },
                        leadingIcon = { Icon(Icons.Outlined.NotInterested, contentDescription = null) },
                        onClick = { showMenu = false }
                    )
                }
            }
        }
    }
}

fun formatViews(views: Long): String {
    return when {
        views >= 1_000_000 -> String.format("%.1fM views", views / 1_000_000.0)
        views >= 1_000 -> String.format("%.1fK views", views / 1_000.0)
        else -> "$views views"
    }
}
