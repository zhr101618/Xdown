package com.example.xdownloader.service

import android.content.Context
import android.content.Intent
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.data.repository.DownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadRepository: DownloadRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private val _downloadTasks = MutableStateFlow<List<DownloadTask>>(emptyList())
    val downloadTasks: StateFlow<List<DownloadTask>> = _downloadTasks.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    init {
        observeDownloads()
    }

    private fun observeDownloads() {
        scope.launch {
            combine(
                downloadRepository.getAllDownloads(),
                downloadRepository.getActiveDownloads()
            ) { all, active ->
                _downloadTasks.value = all
                _isDownloading.value = active.isNotEmpty()
            }.collect {}
        }
    }

    fun getAllDownloads() = downloadRepository.getAllDownloads()

    fun getActiveDownloads() = downloadRepository.getActiveDownloads()

    suspend fun getDownloadById(id: Long) = downloadRepository.getDownloadById(id)

    suspend fun pauseDownload(taskId: Long) {
        downloadRepository.updateDownload(
            downloadRepository.getDownloadById(taskId)?.copy(
                status = DownloadStatus.PAUSED
            ) ?: return
        )
    }

    suspend fun resumeDownload(taskId: Long) {
        downloadRepository.updateDownload(
            downloadRepository.getDownloadById(taskId)?.copy(
                status = DownloadStatus.PENDING
            ) ?: return
        )
        DownloadService.startDownload(context, taskId)
    }

    suspend fun cancelDownload(taskId: Long) {
        downloadRepository.updateDownload(
            downloadRepository.getDownloadById(taskId)?.copy(
                status = DownloadStatus.CANCELLED
            ) ?: return
        )
    }

    suspend fun deleteDownload(taskId: Long) {
        downloadRepository.deleteDownload(taskId)
    }

    suspend fun deleteAllCompleted() {
        downloadRepository.deleteInactiveDownloads()
    }

    fun startDownload(taskId: Long) {
        DownloadService.startDownload(context, taskId)
    }
}
