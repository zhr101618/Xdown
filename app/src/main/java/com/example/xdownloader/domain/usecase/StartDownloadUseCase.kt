package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.data.model.VideoInfo
import com.example.xdownloader.data.model.VideoQuality
import com.example.xdownloader.data.repository.DownloadRepository
import javax.inject.Inject

/**
 * 开始下载
 */
class StartDownloadUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    suspend operator fun invoke(
        videoInfo: VideoInfo,
        quality: VideoQuality
    ): Result<Long> {
        val fileName = "x_video_${videoInfo.tweetId}_${quality.quality}.mp4"

        val downloadTask = DownloadTask(
            videoId = videoInfo.id,
            tweetId = videoInfo.tweetId,
            quality = quality.quality,
            url = quality.url,
            fileName = fileName,
            thumbnailUrl = videoInfo.thumbnailUrl,
            videoTitle = videoInfo.text,
            fileSize = quality.fileSize,
            duration = quality.duration,
            status = DownloadStatus.PENDING,
            progress = 0,
            downloadedBytes = 0
        )

        val id = downloadRepository.insertDownload(downloadTask)
        return Result.success(id)
    }
}
