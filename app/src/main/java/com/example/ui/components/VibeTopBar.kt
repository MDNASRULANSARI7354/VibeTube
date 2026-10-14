package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.model.User
import com.example.ui.theme.VibeRed

@Composable
fun VibeTopBar(
    currentUser: User?,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = VibeRed,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "VibeTube Logo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "VibeTube",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { /* Cast feature */ },
                    modifier = Modifier.testTag("top_cast_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cast,
                        contentDescription = "Cast Screen",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("top_notifications_btn")
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = VibeRed,
                                contentColor = Color.White
                            ) {
                                Text("3", fontSize = 10.sp)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Profile Avatar / Settings
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onProfileClick() }
                        .testTag("top_profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentUser?.avatarUrl != null) {
                        AsyncImage(
                            model = currentUser.avatarUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
