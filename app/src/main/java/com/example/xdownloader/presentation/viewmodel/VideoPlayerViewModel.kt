package com.example.xdownloader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import androidx.lifecycle.viewModelScope
import com.example.xdownloader.data.model.DownloadStatus
import com.example.xdownloader.data.model.DownloadTask
import com.example.xdownloader.data.repository.DownloadRepository
import kotlinx.coroutines.flow.*

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val repository: DownloadRepository
) : ViewModel() {
    
    val completedVideos: StateFlow<List<DownloadTask>> = repository.getAllDownloads()
        .map { tasks -> tasks.filter { it.status == DownloadStatus.COMPLETED } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun openInFolder(context: Context, filePath: String) {
        viewModelScope.launch {
            try {
                if (filePath.isBlank()) {
                    Toast.makeText(context, "文件路径为空", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val uri = Uri.parse(filePath)
                
                // 尝试定位到文件夹逻辑
                val folderUri = if (filePath.startsWith("content://")) {
                    uri
                } else {
                    val file = File(filePath)
                    if (!file.exists()) {
                        Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    // 获取文件所在的目录
                    val parentFile = file.parentFile ?: file
                    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", parentFile)
                }

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(folderUri, "resource/folder")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                // 如果 resource/folder 不支持，尝试使用通用的查看器
                if (intent.resolveActivity(context.packageManager) == null) {
                    intent.setDataAndType(folderUri, "*/*")
                }

                context.startActivity(Intent.createChooser(intent, "在文件夹中查看"))
            } catch (e: Exception) {
                Toast.makeText(context, "打开失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
