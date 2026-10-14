package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.Video
import com.example.ui.components.ExoPlayerView
import com.example.ui.components.VideoCard
import com.example.ui.components.formatViews
import com.example.ui.theme.VibeRed
import com.example.ui.theme.VibeViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    video: Video,
    relatedVideos: List<Video>,
    comments: List<Comment>,
    onBack: () -> Unit,
    onLikeToggle: () -> Unit,
    onDislikeToggle: () -> Unit,
    onSubscribeToggle: () -> Unit,
    onChannelClick: (String) -> Unit,
    onRelatedVideoClick: (Video) -> Unit,
    onAddComment: (String) -> Unit,
    onSaveToPlaylist: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isDownloaded by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky ExoPlayer Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        ) {
            ExoPlayerView(
                videoUrl = video.videoUrl,
                autoPlay = true,
                modifier = Modifier.fillMaxSize()
            )

            // Back button on top of player
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .testTag("video_detail_back_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Scrollable Body
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Video Title
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Views & Upload Date
                    Text(
                        text = "${formatViews(video.viewsCount)} • ${video.publishedAt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row (Like, Dislike, Share, Download, Save)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Like / Dislike Combined Pill
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { onLikeToggle() }
                                        .padding(horizontal = 6.dp)
                                        .testTag("video_detail_like_btn")
                                ) {
                                    Icon(
                                        imageVector = if (video.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = "Like",
                                        tint = if (video.isLiked) VibeRed else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = formatViews(video.likesCount),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (video.isLiked) VibeRed else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                VerticalDivider(
                                    modifier = Modifier.height(16.dp).padding(horizontal = 4.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )

                                IconButton(
                                    onClick = onDislikeToggle,
                                    modifier = Modifier.size(28.dp).testTag("video_detail_dislike_btn")
                                ) {
                                    Icon(
                                        imageVector = if (video.isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                        contentDescription = "Dislike",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Share
                        ActionPill(
                            icon = Icons.Outlined.Share,
                            label = "Share",
                            testTag = "video_detail_share_btn",
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Watch ${video.title} on VibeTube: ${video.videoUrl}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
                            }
                        )

                        // Download / Offline
                        ActionPill(
                            icon = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.Download,
                            label = if (isDownloaded) "Downloaded" else "Download",
                            tint = if (isDownloaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            testTag = "video_detail_download_btn",
                            onClick = { isDownloaded = !isDownloaded }
                        )

                        // Save to Playlist
                        ActionPill(
                            icon = Icons.Outlined.PlaylistAdd,
                            label = "Save",
                            testTag = "video_detail_save_btn",
                            onClick = { onSaveToPlaylist(video) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Channel Header & Subscribe
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChannelClick(video.channelId) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = video.channelAvatarUrl,
                                contentDescription = video.channelTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = video.channelTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (video.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${video.channelSubscribers} subscribers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onSubscribeToggle,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (video.isSubscribed) MaterialTheme.colorScheme.surfaceVariant else VibeRed,
                                contentColor = if (video.isSubscribed) MaterialTheme.colorScheme.onSurface else Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.testTag("video_detail_subscribe_btn")
                        ) {
                            Text(
                                text = if (video.isSubscribed) "Subscribed" else "Subscribe",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description & AI Summary Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                            .testTag("video_detail_description_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isDescriptionExpanded) "Show less" else "...more",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = video.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 2
                            )

                            // AI Summary Section
                            if (video.aiSummary.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = VibeViolet.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VibeViolet.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "AI Summary",
                                                tint = VibeViolet,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "AI Key Takeaways",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = VibeViolet
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = video.aiSummary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            // Chapters List
                            if (isDescriptionExpanded && video.chapters.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Chapters",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                video.chapters.forEach { ch ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = ch.timeFormatted,
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = ch.title,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Comments Preview Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCommentsSheet = true }
                            .testTag("video_detail_comments_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Comments",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${comments.size}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            val topComment = comments.firstOrNull()
                            if (topComment != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        AsyncImage(
                                            model = topComment.authorAvatarUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Text(
                                        text = topComment.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Up Next",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Related Videos List
            items(relatedVideos.filter { it.id != video.id }) { rel ->
                VideoCard(
                    video = rel,
                    onVideoClick = onRelatedVideoClick,
                    onChannelClick = onChannelClick,
                    onSaveToWatchLater = {},
                    onSaveToPlaylist = onSaveToPlaylist
                )
            }
        }

        // Comments Bottom Sheet
        if (showCommentsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Comments (${comments.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("Add a comment...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("detail_comment_input"),
                            shape = RoundedCornerShape(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    onAddComment(newCommentText)
                                    newCommentText = ""
                                }
                            },
                            modifier = Modifier.testTag("detail_comment_send_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Post Comment",
                                tint = VibeRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        items(comments) { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = c.authorAvatarUrl,
                                        contentDescription = c.authorName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Column {
                                    Text(
                                        text = "${c.authorName} • ${c.timeAgo}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = c.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    testTag: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .height(36.dp)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = tint
            )
        }
    }
}
