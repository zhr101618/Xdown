package com.example.xdownloader.data.model

data class VideoInfo(
    val id: String,
    val tweetId: String,
    val author: Author,
    val text: String,
    val videos: List<VideoGroup>,
    val thumbnailUrl: String,
    val duration: Long = 0
)

data class VideoGroup(
    val id: String,
    val thumbnailUrl: String,
    val duration: Long,
    val qualities: List<VideoQuality>
)

data class Author(
    val name: String,
    val username: String,
    val avatarUrl: String
)

data class VideoQuality(
    val quality: String,       // e.g., "720p", "1080p", "360p"
    val resolution: String,     // e.g., "1280x720"
    val bitrate: Int,           // e.g., 1500
    val fileSize: Long,         // in bytes
    val duration: Long = 0,     // in milliseconds
    val url: String,            // download url
    val format: String          // e.g., "mp4"
)

data class DownloadTask(
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
    val status: DownloadStatus,
    val progress: Int,
    val downloadedBytes: Long,
    val filePath: String = "",
    val thumbnailPath: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}
