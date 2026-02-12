package com.example.xdownloader.data.repository

import com.example.xdownloader.data.api.XVideoApiService
import com.example.xdownloader.data.model.Author
import com.example.xdownloader.data.model.VideoGroup
import com.example.xdownloader.data.model.VideoInfo
import com.example.xdownloader.data.model.VideoQuality
import com.example.xdownloader.domain.repository.IVideoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val apiService: XVideoApiService
) : IVideoRepository {

    /**
     * 获取视频信息
     * @param url X视频链接
     * @return VideoInfo 视频信息
     */
    override suspend fun getVideoInfo(url: String): Result<VideoInfo> {
        var lastError: Throwable? = null
        val maxRetries = 3
        
        for (attempt in 1..maxRetries) {
            val apiResult = apiService.getVideoInfo(url)

            val result = apiResult.fold(
                onSuccess = { response ->
                    // 检查 API 是否返回错误
                    if (response.videos.isNullOrEmpty() && response.originalVideo == null) {
                        return@fold Result.failure<VideoInfo>(Exception("未找到视频，可能原因：\n1. 链接不是视频推文\n2. 视频已被删除\n3. 账号是私密的\n4. API 暂时不可用"))
                    }

                    val tweetId = extractTweetId(url) ?: "unknown"

                    // 将视频按组处理（支持多视频）
                    // 在 X/Twitter 中，如果一个推文有多个视频，API 通常会返回多个 video 对象
                    // 如果只有一个视频，qualities 就是该视频的不同分辨率
                    
                    val videoGroups = mutableListOf<VideoGroup>()
                    
                    if (response.videoGroups != null) {
                        // 如果 API 明确返回了分组（针对多视频推文）
                        response.videoGroups.forEachIndexed { groupIndex, groupDto ->
                            val groupQualities = groupDto.videos?.mapNotNull { video ->
                                video.url?.let { videoUrl ->
                                    VideoQuality(
                                        quality = video.quality ?: "Unknown",
                                        resolution = if (video.width != null && video.height != null) "${video.width}x${video.height}" else "Unknown",
                                        bitrate = 1000,
                                        fileSize = video.size ?: 0,
                                        duration = video.duration ?: groupDto.duration ?: response.duration ?: 0,
                                        url = videoUrl,
                                        format = "mp4"
                                    )
                                }
                            }?.sortedWith(compareByDescending<VideoQuality> { 
                                when {
                                    it.quality.contains("1080") -> 1080
                                    it.quality.contains("720") -> 720
                                    it.quality.contains("480") -> 480
                                    it.quality.contains("360") -> 360
                                    it.quality.contains("270") -> 270
                                    it.quality.contains("High", true) -> 1000
                                    it.quality.contains("Medium", true) -> 500
                                    it.quality.contains("Low", true) -> 100
                                    else -> 0
                                }
                            })

                            if (!groupQualities.isNullOrEmpty()) {
                                videoGroups.add(
                                    VideoGroup(
                                        id = groupDto.id ?: "${tweetId}_$groupIndex",
                                        thumbnailUrl = groupDto.thumbnail ?: response.thumbnail ?: "",
                                        duration = groupDto.duration ?: response.duration ?: groupQualities.firstOrNull()?.duration ?: 0,
                                        qualities = groupQualities
                                    )
                                )
                            }
                        }
                    } else if (response.videos != null) {
                        // 某些 API 可能会把多个视频的 quality 混在一起返回，或者按视频分组
                        // 我们先根据 URL 规律或 API 结构尝试分组。
                        // 大多数 API 返回的 response.videos 是同一个视频的不同清晰度。
                        // 如果有多个视频，通常 originalVideo 或 mediaDetails 会体现。
                        
                        // 目前先按单视频多清晰度处理，如果 response.videos 中有明显的分组特征再细分
                        // 或者如果 API 支持多视频，它应该在 videos 列表中体现
                        
                        val qualities = response.videos.mapNotNull { video ->
                            video.url?.let { videoUrl ->
                                VideoQuality(
                                    quality = video.quality ?: "Unknown",
                                    resolution = if (video.width != null && video.height != null) "${video.width}x${video.height}" else "Unknown",
                                    bitrate = 1000,
                                    fileSize = video.size ?: 0,
                                    duration = video.duration ?: response.duration ?: 0,
                                    url = videoUrl,
                                    format = "mp4"
                                )
                            }
                        }.sortedWith(compareByDescending<VideoQuality> { 
                            when {
                                it.quality.contains("1080") -> 1080
                                it.quality.contains("720") -> 720
                                it.quality.contains("480") -> 480
                                it.quality.contains("360") -> 360
                                it.quality.contains("270") -> 270
                                it.quality.contains("High", true) -> 1000
                                it.quality.contains("Medium", true) -> 500
                                it.quality.contains("Low", true) -> 100
                                else -> 0
                            }
                        })

                        if (qualities.isNotEmpty()) {
                            videoGroups.add(
                                VideoGroup(
                                    id = "${tweetId}_0",
                                    thumbnailUrl = response.thumbnail ?: "",
                                    duration = response.duration ?: qualities.firstOrNull()?.duration ?: 0,
                                    qualities = qualities
                                )
                            )
                        }
                    }

                    val videoInfo = VideoInfo(
                        id = tweetId,
                        tweetId = tweetId,
                        author = Author(
                            name = response.author?.name?.takeIf { it.isNotBlank() } ?: "Unknown Author",
                            username = response.author?.username?.takeIf { it.isNotBlank() } ?: "unknown",
                            avatarUrl = response.author?.avatar ?: ""
                        ),
                        text = response.text.takeIf { it.isNotBlank() } ?: "X Video",
                        videos = videoGroups,
                        thumbnailUrl = response.thumbnail ?: "",
                        duration = response.duration ?: videoGroups.firstOrNull()?.duration ?: 0
                    )

                    Result.success(videoInfo)
                },
                onFailure = { error ->
                    Result.failure<VideoInfo>(error)
                }
            )

            if (result.isSuccess) {
                return result
            } else {
                lastError = result.exceptionOrNull()
                // 如果不是网络错误或者 API 明确返回未找到视频，可以考虑是否重试
                if (lastError?.message?.contains("未找到视频") == true) {
                    // 如果明确说未找到，重试可能也没用，但如果是 API 暂时不可用可以重试
                    if (lastError.message!!.contains("API 暂时不可用")) {
                        // 继续重试
                    } else {
                        return result
                    }
                }
                // 等待一小会儿再重试
                if (attempt < maxRetries) {
                    kotlinx.coroutines.delay(1000L * attempt)
                }
            }
        }

        return Result.failure(lastError ?: Exception("解析失败，已重试 $maxRetries 次"))
    }

    /**
     * 从X链接中提取推文ID
     */
    private fun extractTweetId(url: String): String? {
        // 匹配 x.com 或 twitter.com 的链接
        val pattern = """(?:x\.com|twitter\.com)/\w+/status/(\d+)""".toRegex()
        val match = pattern.find(url)
        return match?.groupValues?.get(1)
    }

    /**
     * 解析质量标识
     */
    private fun parseQuality(quality: String): String {
        return when {
            quality.contains("1080", ignoreCase = true) -> "1080p"
            quality.contains("720", ignoreCase = true) -> "720p"
            quality.contains("480", ignoreCase = true) -> "480p"
            quality.contains("360", ignoreCase = true) -> "360p"
            quality.contains("240", ignoreCase = true) -> "240p"
            quality.contains("original", ignoreCase = true) -> "Original"
            else -> quality.replace("_", " ").replace("-", " ")
        }
    }
}
