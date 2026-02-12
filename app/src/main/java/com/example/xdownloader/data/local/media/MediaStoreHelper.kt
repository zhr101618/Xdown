package com.example.xdownloader.data.local.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * MediaStoreHelper - 用于将下载的视频保存到系统媒体库
 */
class MediaStoreHelper(private val context: Context) {

    /**
     * 保存视频文件到系统媒体库
     * @param sourceFile 源文件
     * @param fileName 目标文件名
     * @return 保存后的Uri
     */
    suspend fun saveVideoToMediaStore(
        sourceFile: File,
        fileName: String
    ): Uri = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveVideoApi29AndAbove(sourceFile, fileName)
        } else {
            saveVideoApi28AndBelow(sourceFile, fileName)
        }
    }

    /**
     * Android Q及以上版本使用MediaStore API
     */
    private fun saveVideoApi29AndAbove(sourceFile: File, fileName: String): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/XDownloader")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IllegalStateException("无法创建MediaStore条目")

        uri.let {
            resolver.openOutputStream(it)?.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)
        }

        return uri
    }

    /**
     * Android Q以下版本使用传统文件保存方式
     */
    private fun saveVideoApi28AndBelow(sourceFile: File, fileName: String): Uri {
        val moviesDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MOVIES
        )

        if (!moviesDir.exists()) {
            moviesDir.mkdirs()
        }

        val destFile = File(moviesDir, fileName)
        sourceFile.copyTo(destFile, overwrite = true)

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DATA, destFile.absolutePath)
            put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.SIZE, destFile.length())
            put(
                MediaStore.Video.Media.DATE_ADDED,
                System.currentTimeMillis() / 1000
            )
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IllegalStateException("无法创建MediaStore条目")

        return uri
    }

    /**
     * 删除视频文件
     */
    suspend fun deleteVideo(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取下载目录
     */
    fun getDownloadsDirectory(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ?: context.filesDir
        } else {
            val externalDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            )
            File(externalDir, "XDownloader").apply {
                if (!exists()) mkdirs()
            }
        }
    }
}
