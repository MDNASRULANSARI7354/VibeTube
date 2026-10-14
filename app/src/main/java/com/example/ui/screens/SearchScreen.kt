package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Video
import com.example.ui.components.VideoCard

@Composable
fun SearchScreen(
    videos: List<Video>,
    onBack: () -> Unit,
    onVideoClick: (Video) -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember {
        mutableStateListOf(
            "Laravel video streaming",
            "Jetpack Compose ExoPlayer",
            "Lo-Fi coding beats",
            "Unreal Engine 5",
            "FFmpeg HLS tutorial"
        )
    }

    val searchResults = remember(searchQuery, videos) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            videos.filter {
                it.title.lowercase().contains(q) ||
                it.channelTitle.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("search_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search VibeTube") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input_field")
                )
            }
        }

        // Suggestions / Recent queries
        if (searchQuery.isBlank()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                recentSearches.forEach { term ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchQuery = term }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = term,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            // Search Results
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    items(searchResults, key = { it.id }) { video ->
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
    }
}
