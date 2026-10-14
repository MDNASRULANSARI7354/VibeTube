package com.example.ui.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    isLooping: Boolean = false,
    showCustomControls: Boolean = true,
    onFullScreenToggle: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isBuffering by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var controlsVisible by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showSpeedDialog by remember { mutableStateOf(false) }

    // Initialize ExoPlayer
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            prepare()
            playWhenReady = autoPlay
        }
    }

    // Player state listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                isBuffering = state == Player.STATE_BUFFERING
                if (state == Player.STATE_READY) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Auto-update scrubber position
    LaunchedEffect(exoPlayer, isPlaying) {
        while (isActive) {
            if (exoPlayer.isPlaying) {
                currentPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
                duration = exoPlayer.duration.coerceAtLeast(0L)
            }
            delay(500)
        }
    }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(4000)
            controlsVisible = false
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                controlsVisible = !controlsVisible
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering Indicator
        if (isBuffering) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        // Custom Overlay Controls
        if (showCustomControls) {
            AnimatedVisibility(
                visible = controlsVisible,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.65f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                ) {
                    // Top Bar Controls (Speed / Settings)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clickable { showSpeedDialog = true }
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "${playbackSpeed}x",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = { showSpeedDialog = true },
                            modifier = Modifier.testTag("player_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    }

                    // Center Transport Controls (Rewind 10, Play/Pause, Forward 10)
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                                exoPlayer.seekTo(newPos)
                                currentPosition = newPos
                            },
                            modifier = Modifier.testTag("player_rewind_10_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                if (isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(56.dp)
                                .testTag("player_play_pause_btn")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val max = exoPlayer.duration
                                val newPos = if (max > 0) (exoPlayer.currentPosition + 10000).coerceAtMost(max) else exoPlayer.currentPosition + 10000
                                exoPlayer.seekTo(newPos)
                                currentPosition = newPos
                            },
                            modifier = Modifier.testTag("player_forward_10_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Bottom Bar (Progress slider, time formatted, full screen toggle)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Slider(
                            value = if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f,
                            onValueChange = { ratio ->
                                val seekTarget = (ratio * duration).toLong()
                                currentPosition = seekTarget
                                exoPlayer.seekTo(seekTarget)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("player_progress_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${formatDuration(currentPosition)} / ${formatDuration(duration)}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (onFullScreenToggle != null) {
                                    IconButton(
                                        onClick = onFullScreenToggle,
                                        modifier = Modifier.size(32.dp).testTag("player_fullscreen_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = "Fullscreen",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Speed Selector Dialog
        if (showSpeedDialog) {
            AlertDialog(
                onDismissRequest = { showSpeedDialog = false },
                title = { Text("Playback Speed", style = MaterialTheme.typography.titleMedium) },
                text = {
                    Column {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            TextButton(
                                onClick = {
                                    playbackSpeed = speed
                                    exoPlayer.setPlaybackSpeed(speed)
                                    showSpeedDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (speed == 1.0f) "Normal (1.0x)" else "${speed}x",
                                        color = if (playbackSpeed == speed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (playbackSpeed == speed) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
