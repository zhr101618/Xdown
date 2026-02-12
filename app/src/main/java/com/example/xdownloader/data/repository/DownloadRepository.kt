package com.example.xdownloader.data.repository

import com.example.xdownloader.data.local.database.DownloadDao
import com.example.xdownloader.data.local.database.entities.DownloadTaskEntity
import com.example.xdownloader.data.local.database.entities.toEntity
import com.example.xdownloader.data.local.database.entities.toModel
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.domain.repository.IDownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    private val downloadDao: DownloadDao
) : IDownloadRepository {

    override fun getAllDownloads(): Flow<List<DownloadTask>> {
        return downloadDao.getAllDownloads().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override fun getActiveDownloads(): Flow<List<DownloadTask>> {
        return downloadDao.getActiveDownloads().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun getDownloadById(id: Long): DownloadTask? {
        return downloadDao.getDownloadById(id)?.toModel()
    }

    override suspend fun insertDownload(download: DownloadTask): Long {
        return downloadDao.insertDownload(download.toEntity())
    }

    override suspend fun updateDownload(download: DownloadTask) {
        downloadDao.updateDownload(download.toEntity())
    }

    override suspend fun updateDownloadProgress(
        id: Long,
        status: DownloadStatus,
        progress: Int,
        downloadedBytes: Long
    ) {
        downloadDao.updateDownloadProgress(
            id = id,
            status = status.name,
            progress = progress,
            downloadedBytes = downloadedBytes
        )
    }

    override suspend fun markAsCompleted(
        id: Long,
        filePath: String,
        thumbnailPath: String
    ) {
        downloadDao.markAsCompleted(
            id = id,
            status = DownloadStatus.COMPLETED.name,
            filePath = filePath,
            thumbnailPath = thumbnailPath,
            completedAt = System.currentTimeMillis()
        )
    }

    override suspend fun deleteDownload(id: Long) {
        downloadDao.deleteDownload(id)
    }

    override suspend fun deleteAllDownloads() {
        downloadDao.deleteAllDownloads()
    }

    override suspend fun deleteInactiveDownloads() {
        downloadDao.deleteInactiveDownloads()
    }
}
