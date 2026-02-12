package com.example.xdownloader.data.api.dto

import com.google.gson.annotations.SerializedName

data class VideoInfoResponse(
    @SerializedName("text")
    val text: String = "",
    @SerializedName("author")
    val author: AuthorDto? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("videos")
    val videos: List<VideoDto>? = null,
    @SerializedName("original_video")
    val originalVideo: VideoDto? = null,
    @SerializedName("duration")
    val duration: Long? = null,
    @SerializedName("video_groups")
    val videoGroups: List<VideoGroupDto>? = null
)

data class VideoGroupDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("duration")
    val duration: Long? = null,
    @SerializedName("videos")
    val videos: List<VideoDto>? = null
)

data class AuthorDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null
)

data class VideoDto(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("quality")
    val quality: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("duration")
    val duration: Long? = null
)
