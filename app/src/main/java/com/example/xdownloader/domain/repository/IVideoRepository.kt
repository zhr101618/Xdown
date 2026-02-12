package com.example.xdownloader.domain.repository

import com.example.xdownloader.data.model.VideoInfo
import kotlinx.coroutines.flow.Flow

/**
 * 视频数据仓库接口
 */
interface IVideoRepository {
    suspend fun getVideoInfo(url: String): Result<VideoInfo>
}

/**
 * 下载任务仓库接口
 */
interface IDownloadRepository {
    fun getAllDownloads(): Flow<List<com.example.xdownloader.data.model.DownloadTask>>
    fun getActiveDownloads(): Flow<List<com.example.xdownloader.data.model.DownloadTask>>
    suspend fun getDownloadById(id: Long): com.example.xdownloader.data.model.DownloadTask?
    suspend fun insertDownload(download: com.example.xdownloader.data.model.DownloadTask): Long
    suspend fun updateDownload(download: com.example.xdownloader.data.model.DownloadTask)
    suspend fun updateDownloadProgress(
        id: Long,
        status: com.example.xdownloader.data.model.DownloadStatus,
        progress: Int,
        downloadedBytes: Long
    )
    suspend fun markAsCompleted(id: Long, filePath: String, thumbnailPath: String)
    suspend fun deleteDownload(id: Long)
    suspend fun deleteAllDownloads()
    suspend fun deleteInactiveDownloads()
}
