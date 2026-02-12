package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.repository.DownloadRepository
import javax.inject.Inject

/**
 * 删除下载任务
 */
class DeleteDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(downloadId: Long): Result<Unit> {
        downloadRepository.deleteDownload(downloadId)
        return Result.success(Unit)
    }
}
