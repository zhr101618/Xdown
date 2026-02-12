package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.repository.DownloadRepository
import javax.inject.Inject

/**
 * 继续下载
 */
class ResumeDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(downloadId: Long): Result<Unit> {
        val task = downloadRepository.getDownloadById(downloadId)
            ?: return Result.failure(Exception("下载任务不存在"))

        if (task.status != DownloadStatus.PAUSED) {
            return Result.failure(Exception("只能继续已暂停的任务"))
        }

        val updatedTask = task.copy(status = DownloadStatus.PENDING)
        downloadRepository.updateDownload(updatedTask)
        return Result.success(Unit)
    }
}
