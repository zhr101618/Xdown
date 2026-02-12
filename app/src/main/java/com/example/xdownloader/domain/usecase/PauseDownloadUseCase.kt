package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.repository.DownloadRepository
import javax.inject.Inject

/**
 * 暂停下载
 */
class PauseDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(downloadId: Long): Result<Unit> {
        val task = downloadRepository.getDownloadById(downloadId)
            ?: return Result.failure(Exception("下载任务不存在"))

        if (task.status != DownloadStatus.DOWNLOADING) {
            return Result.failure(Exception("只能暂停正在下载的任务"))
        }

        val updatedTask = task.copy(status = DownloadStatus.PAUSED)
        downloadRepository.updateDownload(updatedTask)
        return Result.success(Unit)
    }
}
