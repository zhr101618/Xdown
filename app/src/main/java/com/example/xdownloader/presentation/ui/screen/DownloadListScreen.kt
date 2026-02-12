package com.example.xdownloader.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.presentation.viewmodel.DownloadListViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.xdownloader.presentation.viewmodel.SortOrder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.xdownloader.presentation.viewmodel.ViewMode
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadListScreen(
    viewModel: DownloadListViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToPlayer: (String, Int) -> Unit = { _, _ -> }
) {
    val downloadTasks by viewModel.downloadTasks.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val currentSortOrder by viewModel.sortOrder.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val context = LocalContext.current

    var showSortMenu by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedIds.size} Selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = "Select All")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Selected")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Download List") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToHome) {
                            Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(imageVector = Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("By Time ${if (currentSortOrder == SortOrder.TIME) "✓" else ""}") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.TIME)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Size ${if (currentSortOrder == SortOrder.SIZE) "✓" else ""}") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.SIZE)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Name ${if (currentSortOrder == SortOrder.NAME) "✓" else ""}") },
                                onClick = {
                                    viewModel.setSortOrder(SortOrder.NAME)
                                    showSortMenu = false
                                }
                            )
                        }

                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (viewMode == ViewMode.LIST) "Grid View" else "List View") },
                                onClick = {
                                    viewModel.setViewMode(if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST)
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Multi-select") },
                                onClick = {
                                    viewModel.toggleSelectionMode()
                                    showMoreMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Clear Completed") },
                                onClick = {
                                    viewModel.deleteAllCompleted()
                                    showMoreMenu = false
                                }
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete ${selectedIds.size} selected tasks?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSelected()
                            showDeleteConfirm = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (downloadTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No Download Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            if (viewMode == ViewMode.LIST) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(downloadTasks, key = { it.id }) { task ->
                        DownloadItem(
                            task = task,
                            isSelectionMode = isSelectionMode,
                            isSelected = selectedIds.contains(task.id),
                            onPause = { viewModel.pauseDownload(task.id) },
                            onResume = { viewModel.resumeDownload(task.id) },
                            onCancel = { viewModel.cancelDownload(task.id) },
                            onDelete = { viewModel.deleteDownload(task.id) },
                            onToggleSelect = { viewModel.toggleSelection(task.id) },
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(task.id)
                                } else if (task.status == DownloadStatus.COMPLETED) {
                                    val completedIndex = downloadTasks
                                        .filter { it.status == DownloadStatus.COMPLETED }
                                        .indexOfFirst { it.id == task.id }
                                    if (completedIndex != -1) {
                                        onNavigateToPlayer(task.filePath, completedIndex)
                                    }
                                }
                            }
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(downloadTasks, key = { it.id }) { task ->
                        DownloadGridItem(
                            task = task,
                            isSelectionMode = isSelectionMode,
                            isSelected = selectedIds.contains(task.id),
                            onPause = { viewModel.pauseDownload(task.id) },
                            onResume = { viewModel.resumeDownload(task.id) },
                            onCancel = { viewModel.cancelDownload(task.id) },
                            onDelete = { viewModel.deleteDownload(task.id) },
                            onToggleSelect = { viewModel.toggleSelection(task.id) },
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(task.id)
                                } else if (task.status == DownloadStatus.COMPLETED) {
                                    val completedIndex = downloadTasks
                                        .filter { it.status == DownloadStatus.COMPLETED }
                                        .indexOfFirst { it.id == task.id }
                                    if (completedIndex != -1) {
                                        onNavigateToPlayer(task.filePath, completedIndex)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadItem(
    task: com.example.xdownloader.data.model.DownloadTask,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                when (task.status) {
                    DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.primaryContainer
                    DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
                    DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail
                    Box(
                        modifier = Modifier
                            .size(100.dp, 70.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumbnailModel = when {
                            task.thumbnailPath.isNotBlank() -> task.thumbnailPath
                            task.thumbnailUrl.isNotBlank() -> task.thumbnailUrl
                            else -> null
                        }
                        
                        if (thumbnailModel != null) {
                            AsyncImage(
                                model = thumbnailModel,
                                contentDescription = "Video thumbnail",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (task.videoTitle.isNotBlank()) task.videoTitle else task.fileName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
                            ),
                            maxLines = 2,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                getStatusText(task.status),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (task.status == DownloadStatus.COMPLETED || task.status == DownloadStatus.FAILED) {
                                val displaySize = if (task.status == DownloadStatus.COMPLETED && task.downloadedBytes > 0) {
                                    task.downloadedBytes
                                } else {
                                    task.fileSize
                                }
                                Text(
                                    formatFileSize(displaySize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (task.duration > 0) {
                                Text(
                                    formatDuration(task.duration),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (!isSelectionMode) {
                        when (task.status) {
                            DownloadStatus.DOWNLOADING -> {
                                Text(
                                    "${task.progress}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            DownloadStatus.FAILED -> {
                                IconButton(onClick = onResume) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }

                if (task.status == DownloadStatus.DOWNLOADING || task.status == DownloadStatus.PAUSED) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = task.progress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${formatFileSize(task.downloadedBytes)} / ${formatFileSize(task.fileSize)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!isSelectionMode) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (task.status) {
                            DownloadStatus.DOWNLOADING -> {
                                OutlinedButton(
                                    onClick = onPause,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Pause, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Pause")
                                }
                                OutlinedButton(
                                    onClick = onCancel,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Cancel")
                                }
                            }
                            DownloadStatus.PAUSED -> {
                                Button(
                                    onClick = onResume,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Resume")
                                }
                                OutlinedButton(
                                    onClick = onCancel,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Cancel")
                                }
                            }
                            DownloadStatus.FAILED -> {
                                Button(
                                    onClick = onResume,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Retry")
                                }
                                OutlinedButton(
                                    onClick = onDelete,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Delete")
                                }
                            }
                            DownloadStatus.COMPLETED -> {
                                OutlinedButton(
                                    onClick = onClick,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Play")
                                }
                                OutlinedButton(
                                    onClick = onDelete,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Delete")
                                }
                            }
                            else -> {
                                OutlinedButton(
                                    onClick = onDelete,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadGridItem(
    task: com.example.xdownloader.data.model.DownloadTask,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                when (task.status) {
                    DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.primaryContainer
                    DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer
                    DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            }
        )
    ) {
        Column {
            // Thumbnail as main feature
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Rectangle shape
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val thumbnailModel = when {
                    task.thumbnailPath.isNotBlank() -> task.thumbnailPath
                    task.thumbnailUrl.isNotBlank() -> task.thumbnailUrl
                    else -> null
                }
                
                if (thumbnailModel != null) {
                    AsyncImage(
                        model = thumbnailModel,
                        contentDescription = "Video thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }

                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f))
                    )
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                }

                if (task.duration > 0) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            formatDuration(task.duration),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (task.videoTitle.isNotBlank()) task.videoTitle else task.fileName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 40.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        getStatusText(task.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (task.status == DownloadStatus.DOWNLOADING) {
                        Text(
                            "${task.progress}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (task.status == DownloadStatus.DOWNLOADING || task.status == DownloadStatus.PAUSED) {
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = task.progress / 100f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun getStatusText(status: DownloadStatus): String {
    return when (status) {
        DownloadStatus.PENDING -> "Pending..."
        DownloadStatus.DOWNLOADING -> "Downloading..."
        DownloadStatus.PAUSED -> "Paused"
        DownloadStatus.COMPLETED -> "Completed"
        DownloadStatus.FAILED -> "Failed"
        DownloadStatus.CANCELLED -> "Cancelled"
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
