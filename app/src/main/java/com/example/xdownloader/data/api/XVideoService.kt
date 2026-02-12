package com.example.xdownloader.data.api

import com.example.xdownloader.data.api.dto.VideoInfoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface XVideoService {

    /**
     * 使用第三方API获取视频信息
     * 支持 savetwitter.net API
     */
    @GET("api/info")
    suspend fun getVideoInfo(
        @Query("url") url: String
    ): VideoInfoResponse

    companion object {
        const val BASE_URL = "https://savetwitter.net/"
    }
}
