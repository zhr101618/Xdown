package com.example.xdownloader.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xdownloader.data.model.VideoInfo
import com.example.xdownloader.data.model.VideoQuality
import com.example.xdownloader.domain.usecase.GetVideoInfoUseCase
import com.example.xdownloader.domain.usecase.ParseXLinkUseCase
import com.example.xdownloader.domain.usecase.StartDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val parseXLinkUseCase: ParseXLinkUseCase,
    private val getVideoInfoUseCase: GetVideoInfoUseCase,
    private val startDownloadUseCase: StartDownloadUseCase,
    private val downloadManager: com.example.xdownloader.service.DownloadManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _link = MutableStateFlow("")
    val link: StateFlow<String> = _link.asStateFlow()

    private val _videoInfo = MutableStateFlow<VideoInfo?>(null)
    val videoInfo: StateFlow<VideoInfo?> = _videoInfo.asStateFlow()

    private val _selectedQualities = MutableStateFlow<Map<String, VideoQuality>>(emptyMap())
    val selectedQualities: StateFlow<Map<String, VideoQuality>> = _selectedQualities.asStateFlow()

    fun onLinkChange(value: String) {
        _link.value = value
    }

    fun pasteFromClipboard(clipboardText: String?) {
        clipboardText?.let {
            _link.value = it
        }
    }

    fun clearLink() {
        _link.value = ""
        _videoInfo.value = null
        _selectedQualities.value = emptyMap()
        _uiState.value = HomeUiState.Idle
    }

    fun parseLink() {
        viewModelScope.launch {
            val url = _link.value.trim()
            if (url.isEmpty()) {
                _uiState.value = HomeUiState.Error("请输入X视频链接")
                return@launch
            }

            _uiState.value = HomeUiState.Loading

            val result = getVideoInfoUseCase(url)
            result.fold(
                onSuccess = { videoInfo ->
                    _videoInfo.value = videoInfo
                    if (videoInfo.videos.isNotEmpty()) {
                        val initialQualities = videoInfo.videos.associate { videoGroup ->
                            videoGroup.id to (videoGroup.qualities.firstOrNull() ?: VideoQuality("", "", 0, 0, 0, "", ""))
                        }.filterValues { it.url.isNotEmpty() }
                        
                        _selectedQualities.value = initialQualities
                        _uiState.value = HomeUiState.VideoLoaded
                    } else {
                        _uiState.value = HomeUiState.Error("未找到可下载的视频")
                    }
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "解析失败")
                }
            )
        }
    }

    fun selectQuality(videoGroupId: String, quality: VideoQuality) {
        val current = _selectedQualities.value.toMutableMap()
        current[videoGroupId] = quality
        _selectedQualities.value = current
    }

    fun toggleVideoSelection(videoGroupId: String) {
        val current = _selectedQualities.value.toMutableMap()
        if (current.containsKey(videoGroupId)) {
            current.remove(videoGroupId)
        } else {
            val videoGroup = _videoInfo.value?.videos?.find { it.id == videoGroupId }
            videoGroup?.qualities?.firstOrNull()?.let {
                current[videoGroupId] = it
            }
        }
        _selectedQualities.value = current
    }

    fun downloadSingle(videoGroupId: String, quality: VideoQuality) {
        viewModelScope.launch {
            val videoInfo = _videoInfo.value ?: return@launch
            val videoGroup = videoInfo.videos.find { it.id == videoGroupId } ?: return@launch

            _uiState.value = HomeUiState.Loading

            val adjustedVideoInfo = videoInfo.copy(
                thumbnailUrl = videoGroup.thumbnailUrl.ifBlank { videoInfo.thumbnailUrl },
                duration = if (videoGroup.duration > 0) videoGroup.duration else videoInfo.duration
            )

            val result = startDownloadUseCase(adjustedVideoInfo, quality)
            result.fold(
                onSuccess = { taskId ->
                    downloadManager.startDownload(taskId)
                    _uiState.value = HomeUiState.DownloadStarted(taskId)
                },
                onFailure = { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "下载启动失败")
                }
            )
        }
    }

    fun startDownload() {
        viewModelScope.launch {
            val videoInfo = _videoInfo.value ?: return@launch
            val targets = _selectedQualities.value

            if (targets.isEmpty()) {
                _uiState.value = HomeUiState.Error("请至少选择一个视频进行下载")
                return@launch
            }

            _uiState.value = HomeUiState.Loading

            var lastTaskId = -1L
            var hasError = false
            
            targets.forEach { (groupId, quality) ->
                // 如果有多个视频，我们需要调整 videoInfo 以匹配特定的视频组信息（如缩略图、时长）
                val videoGroup = videoInfo.videos.find { it.id == groupId } ?: return@forEach
                
                val adjustedVideoInfo = videoInfo.copy(
                    thumbnailUrl = videoGroup.thumbnailUrl.ifBlank { videoInfo.thumbnailUrl },
                    duration = if (videoGroup.duration > 0) videoGroup.duration else videoInfo.duration
                )

                val result = startDownloadUseCase(adjustedVideoInfo, quality)
                result.fold(
                    onSuccess = { taskId ->
                        downloadManager.startDownload(taskId)
                        lastTaskId = taskId
                    },
                    onFailure = { error ->
                        hasError = true
                    }
                )
            }

            if (!hasError && lastTaskId != -1L) {
                _uiState.value = HomeUiState.DownloadStarted(lastTaskId)
            } else if (hasError) {
                _uiState.value = HomeUiState.Error("部分视频下载启动失败")
            }
        }
    }
}

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    object VideoLoaded : HomeUiState()
    data class DownloadStarted(val taskId: Long) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
