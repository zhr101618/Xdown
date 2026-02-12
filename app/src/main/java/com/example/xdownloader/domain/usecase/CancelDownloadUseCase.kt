package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.repository.DownloadRepository
import javax.inject.Inject

/**
 * 取消下载
 */
class CancelDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(downloadId: Long): Result<Unit> {
        val task = downloadRepository.getDownloadById(downloadId)
            ?: return Result.failure(Exception("下载任务不存在"))

        val updatedTask = task.copy(status = DownloadStatus.CANCELLED)
        downloadRepository.updateDownload(updatedTask)
        return Result.success(Unit)
    }
}
