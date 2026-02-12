package com.example.xdownloader.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.xdownloader.data.model.DownloadStatus

@Entity(tableName = "download_tasks")
data class DownloadTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: String,
    val tweetId: String,
    val quality: String,
    val url: String,
    val fileName: String,
    val thumbnailUrl: String,
    val videoTitle: String = "",
    val fileSize: Long,
    val duration: Long = 0,
    val status: String,  // DownloadStatus enum name
    val progress: Int,
    val downloadedBytes: Long,
    val filePath: String = "",
    val thumbnailPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0
)

fun DownloadTaskEntity.toModel() = com.example.xdownloader.data.model.DownloadTask(
    id = id,
    videoId = videoId,
    tweetId = tweetId,
    quality = quality,
    url = url,
    fileName = fileName,
    thumbnailUrl = thumbnailUrl,
    videoTitle = videoTitle,
    fileSize = fileSize,
    duration = duration,
    status = DownloadStatus.valueOf(status),
    progress = progress,
    downloadedBytes = downloadedBytes,
    filePath = filePath,
    thumbnailPath = thumbnailPath,
    createdAt = createdAt,
    completedAt = completedAt
)

fun com.example.xdownloader.data.model.DownloadTask.toEntity() = DownloadTaskEntity(
    id = id,
    videoId = videoId,
    tweetId = tweetId,
    quality = quality,
    url = url,
    fileName = fileName,
    thumbnailUrl = thumbnailUrl,
    videoTitle = videoTitle,
    fileSize = fileSize,
    duration = duration,
    status = status.name,
    progress = progress,
    downloadedBytes = downloadedBytes,
    filePath = filePath,
    thumbnailPath = thumbnailPath,
    createdAt = createdAt,
    completedAt = completedAt
)
