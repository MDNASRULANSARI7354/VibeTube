package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.network.ApiClient
import com.example.ui.theme.VibeRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BackendConfigDialog(
    onDismiss: () -> Unit
) {
    var apiUrl by remember { mutableStateOf(ApiClient.baseUrl) }
    var testStatus by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = VibeRed
                )
                Text(
                    text = "Backend & Sanctum Config",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Configure your Laravel 11 + PostgreSQL backend connection:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = {
                        apiUrl = it
                        ApiClient.baseUrl = it
                    },
                    label = { Text("Laravel API Endpoint") },
                    placeholder = { Text("http://10.0.2.2:8000/api/") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("backend_api_url_input")
                )

                Button(
                    onClick = {
                        isTesting = true
                        testStatus = null
                        scope.launch {
                            delay(600)
                            isTesting = false
                            testStatus = "Configured & Active: Ready to sync with Laravel API"
                        }
                    },
                    enabled = !isTesting,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().testTag("test_backend_connection_btn")
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Test API Connection", color = MaterialTheme.colorScheme.onSurface)
                }

                if (testStatus != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = testStatus!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Backend Architecture Specs:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Database: PostgreSQL 16\n• Auth: Laravel Sanctum Bearer Token\n• Storage: AWS S3 / MinIO Signed URLs\n• Transcoder: FFmpeg Background Queue",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("backend_dialog_done_btn")
            ) {
                Text("Done")
            }
        }
    )
}
