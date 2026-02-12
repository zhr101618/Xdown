package com.example.xdownloader.presentation.ui.screen

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xdownloader.data.model.VideoInfo
import com.example.xdownloader.presentation.viewmodel.HomeUiState
import com.example.xdownloader.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDownloads: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val link by viewModel.link.collectAsState()
    val videoInfo by viewModel.videoInfo.collectAsState()
    val selectedQualities by viewModel.selectedQualities.collectAsState()

    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is HomeUiState.DownloadStarted && !hasNavigated) {
            hasNavigated = true
            onNavigateToDownloads()
        } else if (uiState !is HomeUiState.DownloadStarted) {
            hasNavigated = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("X Video Downloader") },
                actions = {
                    IconButton(onClick = onNavigateToDownloads) {
                        Icon(imageVector = Icons.Filled.History, contentDescription = "History")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = link,
                onValueChange = { viewModel.onLinkChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Paste X video link...") },
                trailingIcon = {
                    if (link.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearLink() }) {
                            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear")
                        }
                    }
                },
                minLines = 3,
                maxLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        viewModel.pasteFromClipboard(clipboard.primaryClip?.getItemAt(0)?.text?.toString())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Outlined.ContentPaste, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Paste")
                }
                Button(
                    onClick = { viewModel.parseLink() },
                    modifier = Modifier.weight(1f),
                    enabled = link.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                    Text("Parse")
                }
            }

            when (uiState) {
                HomeUiState.Idle -> {}
                HomeUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.padding(16.dp))
                        Text("Parsing...")
                    }
                }
                HomeUiState.VideoLoaded -> {
                    videoInfo?.let { videoInfo ->
                        VideoGroupSection(
                            videoInfo = videoInfo,
                            selectedQualities = selectedQualities,
                            onQualitySelected = { videoGroupId, quality ->
                                viewModel.selectQuality(videoGroupId, quality)
                            },
                            onVideoToggle = { videoGroupId ->
                                viewModel.toggleVideoSelection(videoGroupId)
                            },
                            onDownloadClick = { videoGroupId, quality ->
                                viewModel.downloadSingle(videoGroupId, quality)
                            }
                        )
                    }
                    Button(
                        onClick = { viewModel.startDownload() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedQualities.isNotEmpty()
                    ) {
                        Icon(imageVector = Icons.Filled.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        val count = selectedQualities.size
                        Text(if (count > 1) "Download Selected ($count)" else "Start Download")
                    }
                }
                is HomeUiState.DownloadStarted -> {}
                is HomeUiState.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            (uiState as HomeUiState.Error).message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoGroupSection(
    videoInfo: VideoInfo,
    selectedQualities: Map<String, com.example.xdownloader.data.model.VideoQuality>,
    onQualitySelected: (String, com.example.xdownloader.data.model.VideoQuality) -> Unit,
    onVideoToggle: (String) -> Unit,
    onDownloadClick: (String, com.example.xdownloader.data.model.VideoQuality) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (videoInfo.author.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = videoInfo.author.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        videoInfo.author.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "@${videoInfo.author.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (videoInfo.text.isNotEmpty()) {
                Text(
                    videoInfo.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    maxLines = 3
                )
            }
        }

        videoInfo.videos.forEachIndexed { index, videoGroup ->
            val isSelected = selectedQualities.containsKey(videoGroup.id)
            
            Text(
                if (videoInfo.videos.size > 1) "Video ${index + 1}:" else "Select Video Quality:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            videoGroup.qualities.forEach { quality ->
                QualityItem(
                    quality = quality,
                    selected = isSelected && selectedQualities[videoGroup.id] == quality,
                    onClick = { 
                        if (!isSelected) {
                            onVideoToggle(videoGroup.id)
                        }
                        onQualitySelected(videoGroup.id, quality) 
                    },
                    onDownloadClick = {
                        onDownloadClick(videoGroup.id, quality)
                    }
                )
            }
            if (index < videoInfo.videos.size - 1) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityItem(
    quality: com.example.xdownloader.data.model.VideoQuality,
    selected: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = if (selected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    quality.quality,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${quality.resolution}${if (quality.fileSize > 0) " | ${formatFileSize(quality.fileSize)}" else ""}${if (quality.duration > 0) " | ${formatDuration(quality.duration)}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onDownloadClick) {
                Icon(
                    imageVector = Icons.Filled.Download,
                    contentDescription = "Download this quality",
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
        else -> String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024))
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, remainingMinutes, seconds)
    } else {
        String.format("%02d:%02d", remainingMinutes, seconds)
    }
}
