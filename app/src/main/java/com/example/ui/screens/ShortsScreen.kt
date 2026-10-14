package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Comment
import com.example.data.model.ShortItem
import com.example.ui.components.ExoPlayerView
import com.example.ui.components.formatViews
import com.example.ui.theme.VibeRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortsScreen(
    shorts: List<ShortItem>,
    comments: List<Comment>,
    onLikeToggle: (String) -> Unit,
    onSubscribeToggle: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (shorts.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Shorts available", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { shorts.size })
    var showCommentsSheet by remember { mutableStateOf(false) }
    var activeShortId by remember { mutableStateOf(shorts.firstOrNull()?.id ?: "") }
    var newCommentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().testTag("shorts_vertical_pager")
        ) { page ->
            val short = shorts[page]
            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
                // Video Player for Current Page
                if (isCurrentPage) {
                    ExoPlayerView(
                        videoUrl = short.videoUrl,
                        autoPlay = true,
                        isLooping = true,
                        showCustomControls = false,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = short.channelAvatarUrl,
                        contentDescription = short.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vignette gradient for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Right Action Sidebar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Like Button
                    ShortActionButton(
                        icon = if (short.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        tint = if (short.isLiked) VibeRed else Color.White,
                        label = formatViews(short.likesCount),
                        testTag = "short_like_btn_${short.id}",
                        onClick = { onLikeToggle(short.id) }
                    )

                    // Dislike Button
                    ShortActionButton(
                        icon = Icons.Outlined.ThumbDown,
                        tint = Color.White,
                        label = "Dislike",
                        testTag = "short_dislike_btn_${short.id}",
                        onClick = { /* Dislike feedback */ }
                    )

                    // Comments Button
                    ShortActionButton(
                        icon = Icons.Filled.Comment,
                        tint = Color.White,
                        label = formatViews(short.commentsCount),
                        testTag = "short_comments_btn_${short.id}",
                        onClick = {
                            activeShortId = short.id
                            showCommentsSheet = true
                        }
                    )

                    // Share Button
                    ShortActionButton(
                        icon = Icons.Outlined.Share,
                        tint = Color.White,
                        label = "Share",
                        testTag = "short_share_btn_${short.id}",
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Watch ${short.title} on VibeTube: ${short.videoUrl}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Short"))
                        }
                    )

                    // Spinning Vinyl Sound Badge
                    val infiniteTransition = rememberInfiniteTransition()
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(4000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(rotation)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = short.channelAvatarUrl,
                            contentDescription = "Sound Disc",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    }
                }

                // Bottom Content Metadata
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, end = 72.dp, bottom = 24.dp)
                ) {
                    // Channel Info & Subscribe
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            AsyncImage(
                                model = short.channelAvatarUrl,
                                contentDescription = short.channelTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Text(
                            text = short.channelTitle,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Button(
                            onClick = { onSubscribeToggle(short.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (short.isSubscribed) Color.White.copy(alpha = 0.2f) else VibeRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("short_subscribe_btn_${short.id}")
                        ) {
                            Text(
                                text = if (short.isSubscribed) "Subscribed" else "Subscribe",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Title / Caption
                    Text(
                        text = short.title,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Music Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = short.musicTitle,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Comments Bottom Sheet
        if (showCommentsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCommentsSheet = false },
                containerColor = MaterialTheme.colorScheme.surface,
                sheetState = rememberModalBottomSheetState()
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

                    // Comment Input Row
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
                                .testTag("shorts_comment_input"),
                            shape = RoundedCornerShape(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    onAddComment(activeShortId, newCommentText)
                                    newCommentText = ""
                                }
                            },
                            modifier = Modifier.testTag("shorts_comment_send_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Comment",
                                tint = VibeRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comments List
                    comments.forEach { c ->
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

@Composable
private fun ShortActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            fontSize = 11.sp
        )
    }
}
