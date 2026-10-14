package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShortItem
import com.example.data.model.Video
import com.example.ui.components.ShortsShelf
import com.example.ui.components.VideoCard

@Composable
fun HomeScreen(
    videos: List<Video>,
    shorts: List<ShortItem>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onVideoClick: (Video) -> Unit,
    onShortClick: (ShortItem) -> Unit,
    onChannelClick: (String) -> Unit,
    onSaveToWatchLater: (Video) -> Unit,
    onSaveToPlaylist: (Video) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All",
        "Tech & Coding",
        "Music",
        "Gaming",
        "Design",
        "Podcasts",
        "AI Trends"
    )

    val filteredVideos = remember(videos, selectedCategory) {
        if (selectedCategory == "All") videos else videos.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Category Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onCategorySelected(cat) }
                        .testTag("category_chip_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Main Video Feed
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(filteredVideos, key = { _, video -> video.id }) { index, video ->
                VideoCard(
                    video = video,
                    onVideoClick = onVideoClick,
                    onChannelClick = onChannelClick,
                    onSaveToWatchLater = onSaveToWatchLater,
                    onSaveToPlaylist = onSaveToPlaylist
                )

                // Insert Shorts shelf after first video
                if (index == 0 && shorts.isNotEmpty() && selectedCategory == "All") {
                    ShortsShelf(
                        shorts = shorts,
                        onShortClick = onShortClick
                    )
                }
            }
        }
    }
}
