package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoVisibility
import com.example.ui.theme.VibeRed
import com.example.ui.theme.VibeViolet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UploadScreen(
    onBack: () -> Unit,
    onPublishSuccess: (String, String, String, VideoVisibility, List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tech & Coding") }
    var visibility by remember { mutableStateOf(VideoVisibility.PUBLIC) }
    var tagsText by remember { mutableStateOf("Kotlin, Compose, Video") }
    var isUploading by remember { mutableStateOf(false) }
    var uploadStage by remember { mutableStateOf("") }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var uploadComplete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val categories = listOf("Tech & Coding", "Music", "Gaming", "Design", "Podcasts", "AI Trends")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
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
                    modifier = Modifier.testTag("upload_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Upload Video",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            isUploading = true
                            scope.launch {
                                uploadStage = "Requesting signed S3 upload URL..."
                                uploadProgress = 0.2f
                                delay(600)
                                uploadStage = "Streaming video file to S3 bucket..."
                                uploadProgress = 0.5f
                                delay(800)
                                uploadStage = "FFmpeg worker: transcoding 1080p, 720p, 480p renditions..."
                                uploadProgress = 0.8f
                                delay(800)
                                uploadStage = "Generating thumbnails & moderation checks..."
                                uploadProgress = 1.0f
                                delay(500)
                                isUploading = false
                                uploadComplete = true
                                val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                onPublishSuccess(title, description, selectedCategory, visibility, tagsList)
                            }
                        }
                    },
                    enabled = title.isNotBlank() && !isUploading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibeRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("upload_publish_btn")
                ) {
                    Text("Publish", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Body Form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video Preview Placeholder
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = VibeRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "source_recording_1080p.mp4 (48.5 MB)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "H.264 / AAC 60fps • Validated",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Processing Pipeline Status
            if (isUploading) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = VibeViolet.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = VibeViolet
                            )
                            Text(
                                text = uploadStage,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = VibeViolet
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            color = VibeViolet,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (required)") },
                placeholder = { Text("Create a title that describes your video") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_title_field"),
                shape = RoundedCornerShape(12.dp)
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Tell viewers about your video...") },
                minLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_description_field"),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            // Visibility Radio Options
            Text(
                text = "Visibility",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Column {
                listOf(
                    VideoVisibility.PUBLIC to "Public (Anyone can search for and view)",
                    VideoVisibility.UNLISTED to "Unlisted (Anyone with the link can view)",
                    VideoVisibility.PRIVATE to "Private (Only you can view)"
                ).forEach { (vis, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { visibility = vis }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = visibility == vis,
                            onClick = { visibility = vis }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Tags Field
            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Tags (comma separated)") },
                placeholder = { Text("e.g. android, compose, streaming") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upload_tags_field"),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
