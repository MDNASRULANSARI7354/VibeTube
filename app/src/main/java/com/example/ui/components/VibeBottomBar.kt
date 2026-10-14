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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VibeRed

enum class NavTab {
    HOME, SHORTS, SUBSCRIPTIONS, LIBRARY
}

@Composable
fun VibeBottomBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(60.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                icon = if (currentTab == NavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentTab == NavTab.HOME,
                testTag = "nav_home_btn",
                onClick = { onTabSelected(NavTab.HOME) }
            )

            // Shorts
            BottomNavItem(
                icon = if (currentTab == NavTab.SHORTS) Icons.Filled.FlashOn else Icons.Outlined.FlashOn,
                label = "Shorts",
                isSelected = currentTab == NavTab.SHORTS,
                testTag = "nav_shorts_btn",
                onClick = { onTabSelected(NavTab.SHORTS) }
            )

            // Center Upload FAB Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(VibeRed)
                    .clickable { onUploadClick() }
                    .testTag("nav_upload_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload Video",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Subscriptions
            BottomNavItem(
                icon = if (currentTab == NavTab.SUBSCRIPTIONS) Icons.Filled.Subscriptions else Icons.Outlined.Subscriptions,
                label = "Subscriptions",
                isSelected = currentTab == NavTab.SUBSCRIPTIONS,
                testTag = "nav_subscriptions_btn",
                onClick = { onTabSelected(NavTab.SUBSCRIPTIONS) }
            )

            // Library / You
            BottomNavItem(
                icon = if (currentTab == NavTab.LIBRARY) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                label = "You",
                isSelected = currentTab == NavTab.LIBRARY,
                testTag = "nav_library_btn",
                onClick = { onTabSelected(NavTab.LIBRARY) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
