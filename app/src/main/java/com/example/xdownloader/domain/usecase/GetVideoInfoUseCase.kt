package com.example.xdownloader.domain.usecase

import com.example.xdownloader.data.repository.VideoRepository
import com.example.xdownloader.data.model.VideoInfo
import javax.inject.Inject

/**
 * 获取视频信息
 */
class GetVideoInfoUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    private val parseXLinkUseCase: ParseXLinkUseCase
) {

    suspend operator fun invoke(url: String): Result<VideoInfo> {
        // 验证链接
        if (!parseXLinkUseCase.isValidXLink(url)) {
            return Result.failure(Exception("无效的X视频链接"))
        }

        // 获取视频信息
        return videoRepository.getVideoInfo(url)
    }
}
