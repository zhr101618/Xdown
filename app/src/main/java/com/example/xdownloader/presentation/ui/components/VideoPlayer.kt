package com.example.xdownloader.presentation.ui.components

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import kotlinx.coroutines.delay

@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    showControls: Boolean = true,
    onToggleControls: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var playWhenReady by remember { mutableStateOf(true) }
    var playerError by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    // 同步外部激活状态
    LaunchedEffect(isActive) {
        exoPlayer.playWhenReady = isActive && playWhenReady
    }

    LaunchedEffect(uri) {
        try {
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isActive && playWhenReady
            playerError = null
        } catch (e: Exception) {
            playerError = "播放器初始化失败: " + e.message
        }
    }

    LaunchedEffect(playWhenReady, isActive) {
        try {
            exoPlayer.playWhenReady = isActive && playWhenReady
        } catch (e: Exception) {
            playerError = "播放器控制失败: " + e.message
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            try {
                exoPlayer.release()
            } catch (e: Exception) {
                // Ignore release errors
            }
        }
    }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onToggleControls() }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    this.useController = false
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                playerView.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            val error = playerError
            if (error != null) {
                ErrorView(error = error, onRetry = {
                    try {
                        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                        playerError = null
                    } catch (e: Exception) {
                        playerError = "重试失败: " + e.message
                    }
                })
            } else {
                PlayerControls(
                    exoPlayer = exoPlayer,
                    playWhenReady = playWhenReady,
                    onPlayPauseClick = { playWhenReady = !playWhenReady }
                )
            }
        }
    }
}

@Composable
fun PlayerControls(
    exoPlayer: ExoPlayer,
    playWhenReady: Boolean,
    onPlayPauseClick: () -> Unit
) {
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    
    // 使用 derivedStateOf 减少 Slider 重组频率，提高滑动性能
    val sliderValue by remember {
        derivedStateOf {
            if (duration > 0) currentPosition.toFloat() / duration else 0f
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            try {
                if (exoPlayer.isPlaying) {
                    currentPosition = exoPlayer.currentPosition
                    duration = exoPlayer.duration
                }
                delay(30) // 提高到 30ms 一次更新，达到接近 33fps 的丝滑感
            } catch (e: Exception) {
                delay(1000)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        // 居中的播放/暂停按钮
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = if (playWhenReady) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (playWhenReady) "Pause" else "Play",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxSize()
            )
        }

        // 底部进度条
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatTime(currentPosition),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    formatTime(duration),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = sliderValue,
                onValueChange = { value ->
                    currentPosition = (value * duration).toLong() // 拖动时立即反馈
                    exoPlayer.seekTo(currentPosition)
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                    thumbColor = Color.White
                ),
                modifier = Modifier.height(20.dp)
            )
        }
    }
}

@Composable
fun ErrorView(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = error,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "重试",
                    color = Color.White
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
