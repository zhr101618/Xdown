package com.example.xdownloader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.service.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    TIME, SIZE, NAME
}

enum class ViewMode {
    LIST, GRID
}

@HiltViewModel
class DownloadListViewModel @Inject constructor(
    private val downloadManager: DownloadManager
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.TIME)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _viewMode = MutableStateFlow(ViewMode.LIST)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    val downloadTasks: StateFlow<List<DownloadTask>> = combine(
        downloadManager.downloadTasks,
        _sortOrder
    ) { tasks, order ->
        when (order) {
            SortOrder.TIME -> tasks.sortedByDescending { it.id } // 假设 ID 是自增的，代表时间
            SortOrder.SIZE -> tasks.sortedByDescending { it.fileSize }
            SortOrder.NAME -> tasks.sortedBy { if (it.videoTitle.isNotBlank()) it.videoTitle else it.fileName }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
    }

    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedIds.value = emptySet()
        }
    }

    fun toggleSelection(taskId: Long) {
        _selectedIds.value = _selectedIds.value.toMutableSet().apply {
            if (contains(taskId)) remove(taskId) else add(taskId)
        }
    }

    fun selectAll() {
        _selectedIds.value = downloadTasks.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val idsToDelete = _selectedIds.value
            idsToDelete.forEach { id ->
                downloadManager.deleteDownload(id)
            }
            toggleSelectionMode()
        }
    }

    val isDownloading = downloadManager.isDownloading

    init {
        observeDownloads()
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            downloadManager.getAllDownloads().collect {}
        }
    }

    fun pauseDownload(taskId: Long) {
        viewModelScope.launch {
            downloadManager.pauseDownload(taskId)
        }
    }

    fun resumeDownload(taskId: Long) {
        viewModelScope.launch {
            downloadManager.resumeDownload(taskId)
        }
    }

    fun cancelDownload(taskId: Long) {
        viewModelScope.launch {
            downloadManager.cancelDownload(taskId)
        }
    }

    fun deleteDownload(taskId: Long) {
        viewModelScope.launch {
            downloadManager.deleteDownload(taskId)
        }
    }

    fun deleteAllCompleted() {
        viewModelScope.launch {
            downloadManager.deleteAllCompleted()
        }
    }
}
