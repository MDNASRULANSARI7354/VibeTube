package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ShortItem
import com.example.ui.theme.VibeRed

@Composable
fun ShortsShelf(
    shorts: List<ShortItem>,
    onShortClick: (ShortItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Shelf Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Shorts",
                tint = VibeRed,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Shorts",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Horizontal Row of 9:16 Cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(shorts, key = { it.id }) { item ->
                ShortCard(
                    short = item,
                    onClick = { onShortClick(item) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ShortCard(
    short: ShortItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .width(140.dp)
            .height(230.dp)
            .clickable(onClick = onClick)
            .testTag("short_card_${short.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = short.channelAvatarUrl, // Using visual asset as dynamic backdrop
                contentDescription = short.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Text Metadata at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = short.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatViews(short.likesCount * 12)}",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }
        }
    }
}
