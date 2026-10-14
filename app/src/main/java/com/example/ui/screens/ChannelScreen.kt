package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
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
import coil.compose.AsyncImage
import com.example.data.model.Channel
import com.example.data.model.Video
import com.example.ui.components.VideoCard
import com.example.ui.theme.VibeRed

@Composable
fun ChannelScreen(
    channel: Channel,
    videos: List<Video>,
    onBack: () -> Unit,
    onVideoClick: (Video) -> Unit,
    onSubscribeToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Videos", "About")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Channel Top Header with Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = channel.bannerUrl,
                contentDescription = "Channel Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("channel_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Channel Profile Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = channel.avatarUrl,
                        contentDescription = channel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = "${channel.handle} • ${channel.subscriberCount} subscribers • ${channel.videoCount} videos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subscribe Button
            Button(
                onClick = { onSubscribeToggle(channel.id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (channel.isSubscribed) MaterialTheme.colorScheme.surfaceVariant else VibeRed,
                    contentColor = if (channel.isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("channel_subscribe_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (channel.isSubscribed) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (channel.isSubscribed) "Subscribed" else "Subscribe",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // Tab Content
        when (selectedTabIndex) {
            0, 1 -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(videos.filter { it.channelId == channel.id }) { video ->
                        VideoCard(
                            video = video,
                            onVideoClick = onVideoClick,
                            onChannelClick = {},
                            onSaveToWatchLater = {},
                            onSaveToPlaylist = {}
                        )
                    }
                }
            }
            2 -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About this channel",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = channel.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Joined: ${channel.joinedDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
