package com.example.xdownloader.data.api

import com.example.xdownloader.data.api.dto.AuthorDto
import com.example.xdownloader.data.api.dto.VideoDto
import com.example.xdownloader.data.api.dto.VideoGroupDto
import com.example.xdownloader.data.api.dto.VideoInfoResponse
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.FormBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * X视频API服务 - 使用可靠的方法获取视频链接
 */
@Singleton
class XVideoApiService @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 获取视频信息
     */
    suspend fun getVideoInfo(url: String): Result<VideoInfoResponse> = withContext(Dispatchers.IO) {
        val tweetId = extractTweetId(url)
        if (tweetId == null) {
            return@withContext Result.failure(Exception("无效的 X/Twitter 链接"))
        }

        return@withContext tryGetVideo(tweetId)
    }

    /**
     * 从链接中提取推文 ID
     */
    private fun extractTweetId(url: String): String? {
        val pattern = """(?:x\.com|twitter\.com)/[^/]+/status/(\d+)""".toRegex()
        val match = pattern.find(url)
        return match?.groupValues?.get(1)
    }

    /**
     * 尝试获取视频
     */
    private suspend fun tryGetVideo(tweetId: String): Result<VideoInfoResponse> {
        val tweetUrl = "https://x.com/i/status/$tweetId"

        // 方法1: 使用 tweetpik.com API
        val result1 = tryTweetPik(tweetUrl)
        if (result1.isSuccess) return result1

        // 方法2: 使用 savetwitter.net API
        val result2 = trySaveTwitter(tweetUrl)
        if (result2.isSuccess) return result2

        // 方法3: 使用 twitsave.com API
        val result3 = tryTwitSave(tweetUrl)
        if (result3.isSuccess) return result3

        val result4 = trySyndication(tweetId)
        if (result4.isSuccess) return result4

        val result5 = tryFixTwitter(tweetId)
        if (result5.isSuccess) return result5

        // 方法4: 使用 ssstik.io API (适用于18+内容)
        val result6 = trySsstik(tweetUrl)
        if (result6.isSuccess) return result6

        // 方法5: 使用 snapinsta.app API
        val result7 = trySnapinsta(tweetUrl)
        if (result7.isSuccess) return result7

        return Result.failure<VideoInfoResponse>(Exception("无法获取视频信息，可能原因：\n1. 视频已被删除\n2. 账号是私密的\n3. 网络连接问题\n4. API服务暂时不可用\n\n提示：您也可以尝试在其他平台下载视频后，直接输入视频URL进行下载。"))
    }

    private suspend fun trySyndication(tweetId: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://cdn.syndication.twimg.com/tweet-result?id=$tweetId&lang=en"

            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: ""
                val json = JsonParser.parseString(jsonResponse).asJsonObject

                val videoGroups = mutableListOf<VideoGroupDto>()
                var globalThumbnail: String? = null
                var globalDurationMs: Long = 0

                // 解析 mediaDetails 获取多视频分组
                val mediaDetails = json.get("mediaDetails")?.takeIf { it.isJsonArray }?.asJsonArray
                mediaDetails?.forEachIndexed { mediaIndex, item ->
                    val media = item.asJsonObject
                    val type = media.get("type")?.asString ?: ""
                    if (type == "video" || type == "animated_gif") {
                        val mediaThumbnail = media.get("media_url_https")?.asString
                            ?: media.get("media_url")?.asString
                        
                        if (globalThumbnail == null) globalThumbnail = mediaThumbnail
                        
                        val videoInfo = media.get("video_info")?.asJsonObject
                        val durationMs = videoInfo?.get("duration_millis")?.asLong ?: 0L
                        if (globalDurationMs == 0L) globalDurationMs = durationMs
                        
                        val variants = videoInfo?.get("variants")?.asJsonArray
                        val groupVideos = mutableListOf<VideoDto>()
                        val seenUrls = mutableSetOf<String>()
                        
                        variants?.forEach { v ->
                            val variant = v.asJsonObject
                            val contentType = variant.get("content_type")?.asString ?: ""
                            val url = variant.get("url")?.asString
                            if (contentType.contains("mp4", ignoreCase = true) && !url.isNullOrBlank()) {
                                if (url !in seenUrls) {
                                    seenUrls.add(url)
                                    groupVideos.add(createVideoDto(url, groupVideos.size, durationMs))
                                }
                            }
                        }
                        
                        if (groupVideos.isNotEmpty()) {
                            videoGroups.add(
                                VideoGroupDto(
                                    id = "${tweetId}_$mediaIndex",
                                    thumbnail = mediaThumbnail,
                                    duration = durationMs,
                                    videos = groupVideos
                                )
                            )
                        }
                    }
                }

                val userObj = json.get("user")?.takeIf { it.isJsonObject }?.asJsonObject
                val author = if (userObj != null) {
                    AuthorDto(
                        name = userObj.get("name")?.asString ?: "",
                        username = userObj.get("screen_name")?.asString ?: "",
                        avatar = userObj.get("profile_image_url_https")?.asString
                    )
                } else {
                    AuthorDto(name = "", username = "", avatar = null)
                }

                if (videoGroups.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = json.get("text")?.asString ?: "",
                            author = author,
                            thumbnail = globalThumbnail,
                            videoGroups = videoGroups,
                            videos = null,
                            originalVideo = null,
                            duration = globalDurationMs
                        )
                    )
                }
            }

            Result.failure(Exception("Syndication解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("Syndication解析失败: ${e.message}"))
        }
    }

    private suspend fun tryFixTwitter(tweetId: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://api.fxtwitter.com/status/$tweetId"

            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: ""
                val json = JsonParser.parseString(jsonResponse).asJsonObject
                val code = json.get("code")?.takeIf { it.isJsonPrimitive }?.asInt
                if (code != 200) {
                    return Result.failure(Exception("FixTwitter解析失败: code=$code"))
                }

                val tweet = json.get("tweet")?.takeIf { it.isJsonObject }?.asJsonObject
                    ?: return Result.failure(Exception("FixTwitter解析失败: tweet为空"))
                val media = tweet.get("media")?.takeIf { it.isJsonObject }?.asJsonObject
                val videosArray = media?.get("videos")?.takeIf { it.isJsonArray }?.asJsonArray
                val allArray = media?.get("all")?.takeIf { it.isJsonArray }?.asJsonArray
                val sourceArray = videosArray ?: allArray

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null
                val durationMs = tweet.get("duration_ms")?.takeIf { it.isJsonPrimitive }?.asLong ?: 0L

                sourceArray?.forEach { item ->
                    if (!item.isJsonObject) return@forEach
                    val obj = item.asJsonObject
                    val type = obj.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: ""
                    val url = obj.get("url")?.takeIf { it.isJsonPrimitive }?.asString
                    val thumb = obj.get("thumbnail_url")?.takeIf { it.isJsonPrimitive }?.asString
                    if (!thumb.isNullOrBlank() && thumbnail.isNullOrBlank()) {
                        thumbnail = thumb
                    }
                    if (!url.isNullOrBlank() && (type.isBlank() || type.contains("video", true) || type.contains("gif", true))) {
                        if (url.startsWith("http") && url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size, durationMs))
                        }
                    }
                }

                val author = AuthorDto(
                    name = tweet.get("author")?.asJsonObject?.get("name")?.asString ?: "",
                    username = tweet.get("author")?.asJsonObject?.get("screen_name")?.asString ?: "",
                    avatar = tweet.get("author")?.asJsonObject?.get("avatar_url")?.asString
                )

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = tweet.get("text")?.asString ?: "",
                            author = author,
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("FixTwitter解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("FixTwitter解析失败: ${e.message}"))
        }
    }

    /**
     * 使用 tweetpik.com API
     */
    private suspend fun tryTweetPik(tweetUrl: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://tweetpik.com/api/ajax"

            val requestBody = FormBody.Builder()
                .add("id_or_url", tweetUrl)
                .add("tweet", tweetUrl)
                .build()

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Referer", "https://tweetpik.com/")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: ""

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null

                // 提取缩略图
                val thumbnailPattern = """"thumbnail"\s*:\s*"([^"]+)"""".toRegex()
                thumbnailPattern.find(jsonResponse)?.let {
                    thumbnail = it.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                }

                // 模式1: JSON格式中的video_url字段
                val jsonUrlPattern = """"video_url"\s*:\s*"([^"]+)"""".toRegex()
                jsonUrlPattern.findAll(jsonResponse).forEach { match ->
                    val url = match.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                    if (url != null && url.startsWith("http") && url !in seenUrls) {
                        seenUrls.add(url)
                        videos.add(createVideoDto(url, videos.size))
                    }
                }

                // 模式2: 标准的 twimg.com 链接
                if (videos.isEmpty()) {
                    val twimgPattern = """https://[a-zA-Z0-9\-]+\.twimg\.com/tweet_video/[a-zA-Z0-9_\-]+\.mp4[^"'\s]*""".toRegex()
                    twimgPattern.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 模式3: 更宽泛的 twimg.com 视频链接
                if (videos.isEmpty()) {
                    val twimgPattern2 = """https://[a-zA-Z0-9\-]+\.twimg\.com/[^"'\s]+\.mp4[^"'\s]*""".toRegex()
                    twimgPattern2.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 提取作者信息和推文内容
                val authorName = """"name"\s*:\s*"([^"]+)"""".toRegex().find(jsonResponse)?.groupValues?.get(1)?.unescapeJson() ?: ""
                val screenName = """"screen_name"\s*:\s*"([^"]+)"""".toRegex().find(jsonResponse)?.groupValues?.get(1)?.unescapeJson() ?: ""
                val avatar = """"profile_image_url_https"\s*:\s*"([^"]+)"""".toRegex().find(jsonResponse)?.groupValues?.get(1)?.unescapeJson()
                val tweetText = """"text"\s*:\s*"([^"]+)"""".toRegex().find(jsonResponse)?.groupValues?.get(1)?.unescapeJson() ?: ""

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = tweetText,
                            author = AuthorDto(name = authorName, username = screenName, avatar = avatar),
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("TweetPik解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("TweetPik解析失败: ${e.message}"))
        }
    }

    /**
     * 使用 savetwitter.net API
     */
    private suspend fun trySaveTwitter(tweetUrl: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://savetwitter.net/api/ajax"

            val requestBody = FormBody.Builder()
                .add("id", tweetUrl)
                .add("locale", "en")
                .build()

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Referer", "https://savetwitter.net/")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Origin", "https://savetwitter.net")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: ""

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null

                // 提取缩略图
                val thumbnailPattern = """"thumbnail"\s*:\s*"([^"]+)"""".toRegex()
                thumbnailPattern.find(jsonResponse)?.let {
                    thumbnail = it.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                }

                // 模式1: JSON格式中的url字段
                val jsonUrlPattern = """"url"\s*:\s*"([^"]+)"""".toRegex()
                jsonUrlPattern.findAll(jsonResponse).forEach { match ->
                    val url = match.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                    if (url != null && url.startsWith("http") && url.endsWith(".mp4") && url !in seenUrls) {
                        seenUrls.add(url)
                        videos.add(createVideoDto(url, videos.size))
                    }
                }

                // 模式2: 标准的 twimg.com 链接
                if (videos.isEmpty()) {
                    val twimgPattern = """https://[a-zA-Z0-9\-]+\.twimg\.com/tweet_video/[a-zA-Z0-9_\-]+\.mp4[^"'\s]*""".toRegex()
                    twimgPattern.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 模式3: 更宽泛的视频链接
                if (videos.isEmpty()) {
                    val videoPattern = """https://[^\s"']+\.twimg\.com/[^\s"']+\.mp4[^\s"']*""".toRegex()
                    videoPattern.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = "",
                            author = AuthorDto(name = "", username = "", avatar = null),
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("SaveTwitter解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("SaveTwitter解析失败: ${e.message}"))
        }
    }

    /**
     * 使用 twitsave.com API
     */
    private suspend fun tryTwitSave(tweetUrl: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://twitsave.com/info?url=${java.net.URLEncoder.encode(tweetUrl, "UTF-8")}"

            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("Referer", "https://twitsave.com/")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string() ?: ""

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null

                // 提取缩略图
                val thumbnailPattern = """"thumbnail"\s*:\s*"([^"]+)"""".toRegex()
                thumbnailPattern.find(html)?.let {
                    thumbnail = it.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                }

                // 模式1: JSON数据中的视频链接
                val jsonPattern = """"url"\s*:\s*"([^"]+\.mp4[^"]*)" """.toRegex()
                jsonPattern.findAll(html).forEach { match ->
                    val url = match.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                    if (url != null && url.startsWith("http") && url !in seenUrls) {
                        seenUrls.add(url)
                        videos.add(createVideoDto(url, videos.size))
                    }
                }

                // 模式2: href属性中的链接
                if (videos.isEmpty()) {
                    val hrefPattern = """href="(https://[^"]+\.mp4[^"]*)" """.toRegex()
                    hrefPattern.findAll(html).forEach { match ->
                        val url = match.groupValues[1]
                        if (url.startsWith("http") && url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 模式3: 标准的 twimg.com 链接
                if (videos.isEmpty()) {
                    val twimgPattern = """https://[a-zA-Z0-9\-]+\.twimg\.com/tweet_video/[a-zA-Z0-9_\-]+\.mp4[^"'\s]*""".toRegex()
                    twimgPattern.findAll(html).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 模式4: 更宽泛的视频链接
                if (videos.isEmpty()) {
                    val videoPatterns = listOf(
                        """href="(https://[^"]+\.mp4[^"]*)"""".toRegex(),
                        """src="(https://[^"]+\.mp4[^"]*)"""".toRegex(),
                        """url":"(https://[^"]+\.mp4[^"]*)"""".toRegex()
                    )

                    for (pattern in videoPatterns) {
                        pattern.findAll(html).forEach { match ->
                            val url = match.groupValues[1]
                            if (url.isNotBlank() && url.startsWith("http") && url !in seenUrls) {
                                seenUrls.add(url)
                                videos.add(createVideoDto(url, videos.size))
                            }
                        }
                    }
                }

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = "",
                            author = AuthorDto(name = "", username = "", avatar = null),
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("TwitSave解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("TwitSave解析失败: ${e.message}"))
        }
    }

    /**
     * 使用 ssstik.io API (适用于18+内容)
     */
    private suspend fun trySsstik(tweetUrl: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://ssstik.io/en"

            val requestBody = FormBody.Builder()
                .add("url", tweetUrl)
                .build()

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Referer", "https://ssstik.io/")
                .header("Accept", "*/*")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string() ?: ""

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null

                // 提取缩略图
                val thumbnailPattern = """"(https://[^"]+?(?:jpg|png|jpeg))"""".toRegex()
                thumbnailPattern.find(html)?.let {
                    thumbnail = it.groupValues[1].takeIf { it.isNotEmpty() }
                }

                // 模式1: 标准的 twimg.com 链接
                val twimgPattern = """https://[a-zA-Z0-9\-]+\.twimg\.com/tweet_video/[a-zA-Z0-9_\-]+\.mp4[^"'\s]*""".toRegex()
                twimgPattern.findAll(html).forEach { match ->
                    val url = match.value
                    if (url !in seenUrls) {
                        seenUrls.add(url)
                        videos.add(createVideoDto(url, videos.size))
                    }
                }

                // 模式2: 更宽泛的视频链接
                if (videos.isEmpty()) {
                    val videoPatterns = listOf(
                        """href="(https://[^"]+\.mp4[^"]*)"""".toRegex(),
                        """src="(https://[^"]+\.mp4[^"]*)"""".toRegex(),
                        """url":"(https://[^"]+\.mp4[^"]*)"""".toRegex()
                    )

                    for (pattern in videoPatterns) {
                        pattern.findAll(html).forEach { match ->
                            val url = match.groupValues[1]
                            if (url.isNotBlank() && url.startsWith("http") && url !in seenUrls) {
                                seenUrls.add(url)
                                videos.add(createVideoDto(url, videos.size))
                            }
                        }
                    }
                }

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = "",
                            author = AuthorDto(name = "", username = "", avatar = null),
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("Ssstik解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("Ssstik解析失败: ${e.message}"))
        }
    }

    /**
     * 使用 snapinsta.app API
     */
    private suspend fun trySnapinsta(tweetUrl: String): Result<VideoInfoResponse> {
        return try {
            val apiUrl = "https://snapinsta.app/api/ajax"

            val requestBody = FormBody.Builder()
                .add("url", tweetUrl)
                .add("action", "video")
                .build()

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .header("Referer", "https://snapinsta.app/")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() ?: ""

                val videos = mutableListOf<VideoDto>()
                val seenUrls = mutableSetOf<String>()
                var thumbnail: String? = null

                // 提取缩略图
                val thumbnailPattern = """"thumbnail"\s*:\s*"([^"]+)"""".toRegex()
                thumbnailPattern.find(jsonResponse)?.let {
                    thumbnail = it.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                }

                // 模式1: JSON格式中的url字段
                val jsonUrlPattern = """"url"\s*:\s*"([^"]+)"""".toRegex()
                jsonUrlPattern.findAll(jsonResponse).forEach { match ->
                    val url = match.groupValues[1].takeIf { it.isNotEmpty() }?.unescapeJson()
                    if (url != null && url.startsWith("http") && url !in seenUrls) {
                        seenUrls.add(url)
                        videos.add(createVideoDto(url, videos.size))
                    }
                }

                // 模式2: 标准的 twimg.com 链接
                if (videos.isEmpty()) {
                    val twimgPattern = """https://[a-zA-Z0-9\-]+\.twimg\.com/tweet_video/[a-zA-Z0-9_\-]+\.mp4[^"'\s]*""".toRegex()
                    twimgPattern.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                // 模式3: 更宽泛的视频链接
                if (videos.isEmpty()) {
                    val videoPattern = """https://[^\s"']+\.twimg\.com/[^\s"']+\.mp4[^\s"']*""".toRegex()
                    videoPattern.findAll(jsonResponse).forEach { match ->
                        val url = match.value
                        if (url !in seenUrls) {
                            seenUrls.add(url)
                            videos.add(createVideoDto(url, videos.size))
                        }
                    }
                }

                if (videos.isNotEmpty()) {
                    return Result.success(
                        VideoInfoResponse(
                            text = "",
                            author = AuthorDto(name = "", username = "", avatar = null),
                            thumbnail = thumbnail,
                            videos = videos,
                            originalVideo = null
                        )
                    )
                }
            }

            Result.failure(Exception("Snapinsta解析失败"))
        } catch (e: Exception) {
            Result.failure(Exception("Snapinsta解析失败: ${e.message}"))
        }
    }

    /**
     * 解析JSON转义字符
     */
    private fun String.unescapeJson(): String {
        return this.replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\/", "/")
    }

    /**
     * 获取文件大小
     */
    private suspend fun getFileSize(url: String): Long = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .head()
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return@withContext response.header("Content-Length")?.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext 0L
    }

    /**
     * 创建VideoDto
     */
    private suspend fun createVideoDto(url: String, index: Int, duration: Long = 0): VideoDto {
        // 尝试从 URL 中解析质量
        val quality = when {
            url.contains("/1080x") || url.contains("_1080.") || url.contains("?tag=14") -> "1080p"
            url.contains("/720x") || url.contains("_720.") || url.contains("?tag=13") -> "720p"
            url.contains("/480x") || url.contains("_480.") || url.contains("?tag=12") -> "480p"
            url.contains("/360x") || url.contains("_360.") || url.contains("?tag=10") -> "360p"
            url.contains("/270x") || url.contains("_270.") || url.contains("?tag=9") -> "270p"
            else -> {
                // 如果 URL 中没写，可能是 tweetpik 等返回的顺序
                when (index) {
                    0 -> "High Quality"
                    1 -> "Medium Quality"
                    else -> "Low Quality"
                }
            }
        }
        
        // 解析分辨率
        val resolution = when (quality) {
            "1080p" -> "1920x1080"
            "720p" -> "1280x720"
            "480p" -> "854x480"
            "360p" -> "640x360"
            "270p" -> "480x270"
            else -> null
        }

        val size = getFileSize(url)

        return VideoDto(
            url = url,
            quality = quality,
            type = "mp4",
            size = size,
            duration = duration,
            width = resolution?.split("x")?.get(0)?.toIntOrNull(),
            height = resolution?.split("x")?.get(1)?.toIntOrNull()
        )
    }
}
