package com.example.xdownloader.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.xdownloader.R
import com.example.xdownloader.data.local.media.MediaStoreHelper
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.data.repository.DownloadRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : Service() {

    @Inject
    lateinit var downloadRepository: DownloadRepository

    @Inject
    lateinit var mediaStoreHelper: MediaStoreHelper

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var currentJob: Job? = null
    private var isRunning = false

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_PAUSE = "pause_download"
        private const val ACTION_CANCEL = "cancel_download"
        private const val EXTRA_TASK_ID = "task_id"

        fun startDownload(context: Context, taskId: Long) {
            val intent = Intent(context, DownloadService::class.java).apply {
                putExtra(EXTRA_TASK_ID, taskId)
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getLongExtra(EXTRA_TASK_ID, -1) ?: -1

        if (taskId != -1L && !isRunning) {
            isRunning = true
            startForeground(NOTIFICATION_ID, createDownloadNotification(null, 0))
            startDownloadTask(taskId)
        }

        return START_NOT_STICKY
    }

    private fun startDownloadTask(taskId: Long) {
        currentJob = serviceScope.launch {
            var retryCount = 0
            val maxRetries = 3
            var success = false

            while (retryCount < maxRetries && !success) {
                try {
                    // 获取下载任务
                    val task = downloadRepository.getDownloadById(taskId)

                    if (task == null) {
                        downloadRepository.updateDownloadProgress(
                            taskId,
                            DownloadStatus.FAILED,
                            0,
                            0
                        )
                        return@launch
                    }

                    // 标记为下载中
                    downloadRepository.updateDownloadProgress(
                        taskId,
                        DownloadStatus.DOWNLOADING,
                        task.progress,
                        task.downloadedBytes
                    )

                    // 创建下载目录
                    val downloadsDir = mediaStoreHelper.getDownloadsDirectory()

                    // 下载缩略图
                    var thumbnailPath = task.thumbnailPath
                    if (thumbnailPath.isBlank() && task.thumbnailUrl.isNotBlank()) {
                        thumbnailPath = downloadThumbnail(task.thumbnailUrl, downloadsDir, task.tweetId)
                    }

                    val tempFile = File(downloadsDir, "temp_${task.fileName}")

                    // 创建带有必要请求头的 OkHttpClient
                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor { chain ->
                            val original = chain.request()
                            val requestBuilder = original.newBuilder()
                                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36")
                                .header("Accept", "video/mp4,video/webm,video/*")
                                .header("Referer", "https://x.com/")
                                .method(original.method, original.body)
                            chain.proceed(requestBuilder.build())
                        }
                        .build()

                    // 检查是否有已下载的部分
                    var downloadedBytes: Long = task.downloadedBytes
                    val requestBuilder = Request.Builder().url(task.url)

                    if (downloadedBytes > 0 && tempFile.exists()) {
                        requestBuilder.header("Range", "bytes=$downloadedBytes-")
                    }

                    val request = requestBuilder.build()
                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) {
                        if (response.code == 416) {
                            // Requested Range Not Satisfiable - 可能是文件已下载完或损坏
                            downloadedBytes = 0
                            tempFile.delete()
                            // 重新请求
                            continue
                        }
                        throw Exception("下载失败: ${response.code}")
                    }

                    val responseBody = response.body ?: throw Exception("响应体为空")
                    val totalBytes = (if (response.code == 206) responseBody.contentLength() + downloadedBytes else responseBody.contentLength()).coerceAtLeast(task.fileSize)

                    // 更新通知
                    updateNotification(task, downloadedBytes.toInt(), totalBytes.toInt())

                    // 写入文件
                    val append = response.code == 206
                    tempFile.sink(append = append).buffer().use { sink ->
                        val buffer = ByteArray(8192)
                        var read: Int
                        val inputStream = responseBody.byteStream()
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            if (!isRunning) break
                            sink.write(buffer, 0, read)
                            downloadedBytes = downloadedBytes + read

                            // 更新进度 (减少更新频率以提高性能)
                            val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                            if (progress > task.progress) {
                                downloadRepository.updateDownloadProgress(
                                    taskId,
                                    DownloadStatus.DOWNLOADING,
                                    progress,
                                    downloadedBytes
                                )
                                // 更新通知
                                updateNotification(task, downloadedBytes.toInt(), totalBytes.toInt())
                            }
                        }
                    }

                    if (!isRunning) return@launch

                    // 保存到媒体库
                    val finalFile = File(downloadsDir, task.fileName)
                    if (tempFile.exists()) {
                        tempFile.renameTo(finalFile)
                    }

                    val mediaUri = mediaStoreHelper.saveVideoToMediaStore(finalFile, task.fileName)

                    // 如果缩略图还是空的，尝试从下载好的视频中提取
                    if (thumbnailPath.isBlank()) {
                        thumbnailPath = extractThumbnailFromVideo(finalFile, downloadsDir, task.tweetId)
                    }

                    // 标记为已完成 - 使用 MediaStore URI 和缩略图路径
                    downloadRepository.markAsCompleted(taskId, mediaUri.toString(), thumbnailPath)

                    // 更新通知
                    updateCompletedNotification(task)
                    success = true

                } catch (e: Exception) {
                    retryCount++
                    if (retryCount >= maxRetries) {
                        downloadRepository.updateDownloadProgress(
                            taskId,
                            DownloadStatus.FAILED,
                            0,
                            0
                        )
                        e.printStackTrace()
                    } else {
                        // 等待一会再重试
                        kotlinx.coroutines.delay(2000L * retryCount)
                    }
                }
            }

            if (!success && isRunning) {
                stopForeground(true)
                stopSelf()
                isRunning = false
            } else if (success) {
                stopForeground(true)
                stopSelf()
                isRunning = false
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "下载通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示下载进度"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createDownloadNotification(task: DownloadTask?, progress: Int): Notification {
        val title = if (task != null) "下载中: ${task.fileName}" else "准备下载..."

        val pauseIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, DownloadService::class.java).apply {
                action = ACTION_PAUSE
                putExtra(EXTRA_TASK_ID, task?.id ?: 0)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("$progress%")
            .setSmallIcon(R.drawable.ic_download)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(R.drawable.ic_pause, "暂停", pauseIntent)
            .setProgress(100, progress, false)
            .build()
    }

    private fun updateNotification(task: DownloadTask, downloadedBytes: Int, totalBytes: Int) {
        val progress = if (totalBytes > 0) {
            ((downloadedBytes * 100) / totalBytes).toInt()
        } else {
            0
        }

        val notification = createDownloadNotification(task, progress)
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    private fun updateCompletedNotification(task: DownloadTask) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("下载完成")
            .setContentText(task.fileName)
            .setSmallIcon(R.drawable.ic_download_done)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * 下载缩略图
     */
    private suspend fun downloadThumbnail(thumbnailUrl: String, downloadsDir: File, tweetId: String): String {
        var retryCount = 0
        val maxRetries = 3
        
        while (retryCount < maxRetries) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(thumbnailUrl)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful && response.body != null) {
                    val extension = when {
                        thumbnailUrl.contains(".jpg") -> ".jpg"
                        thumbnailUrl.contains(".png") -> ".png"
                        thumbnailUrl.contains(".jpeg") -> ".jpeg"
                        else -> ".jpg"
                    }
                    val thumbnailFile = File(downloadsDir, "thumb_${tweetId}$extension")
                    thumbnailFile.outputStream().use { output ->
                        response.body!!.byteStream().use { input ->
                            input.copyTo(output)
                        }
                    }
                    return thumbnailFile.absolutePath
                }
            } catch (e: Exception) {
                retryCount++
                if (retryCount < maxRetries) {
                    kotlinx.coroutines.delay(1000L * retryCount)
                }
            }
        }
        return ""
    }

    /**
     * 从视频文件中提取缩略图
     */
    private fun extractThumbnailFromVideo(videoFile: File, downloadsDir: File, tweetId: String): String {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(videoFile.absolutePath)
            val bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC) 
                ?: retriever.frameAtTime
            
            if (bitmap != null) {
                val thumbnailFile = File(downloadsDir, "thumb_${tweetId}_extracted.jpg")
                thumbnailFile.outputStream().use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }
                bitmap.recycle()
                thumbnailFile.absolutePath
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
