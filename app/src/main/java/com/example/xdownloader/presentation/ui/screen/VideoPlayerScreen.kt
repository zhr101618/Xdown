package com.example.xdownloader.presentation.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.xdownloader.presentation.ui.components.VideoPlayer
import com.example.xdownloader.presentation.viewmodel.VideoPlayerViewModel

import android.content.Intent
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Folder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.view.WindowInsetsControllerCompat
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun VideoPlayerScreen(
    videoPath: String,
    initialIndex: Int = 0,
    onBack: () -> Unit = {},
    viewModel: VideoPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val completedVideos by viewModel.completedVideos.collectAsState()
    
    // 更多选项菜单状态
    var showMenu by remember { mutableStateOf(false) }
    
    // 控制 UI 按钮和系统状态栏的同步显隐
    var showControls by remember { mutableStateOf(true) }

    // 同步控制系统状态栏
    LaunchedEffect(showControls) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (showControls) {
            controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        } else {
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    // 找到当前播放视频在列表中的索引 - 现在从导航参数传入
    
    // 如果是从下载列表进来的，我们需要等待列表加载完成以确保 Pager 定位正确
    if (completedVideos.isEmpty() && videoPath.isNotEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val pageCount = completedVideos.size.coerceAtLeast(if (videoPath.isNotEmpty()) 1 else 0)
    
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (pageCount - 1).coerceAtLeast(0)),
        pageCount = { pageCount }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondBoundsPageCount = 1
        ) { page ->
            val currentVideoPath = if (completedVideos.isNotEmpty()) {
                completedVideos[page].filePath
            } else {
                videoPath
            }

            if (currentVideoPath != null) {
                val videoUri = remember(currentVideoPath) {
                    if (currentVideoPath.startsWith("content://")) {
                        Uri.parse(currentVideoPath)
                    } else {
                        Uri.fromFile(java.io.File(currentVideoPath))
                    }
                }

                VideoPlayer(
                    uri = videoUri,
                    modifier = Modifier.fillMaxSize(),
                    isActive = pagerState.currentPage == page,
                    showControls = showControls,
                    onToggleControls = { showControls = !showControls }
                )
            }
        }

        // 顶层 UI 控制层
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // 悬浮返回按钮 (顶部状态栏适配)
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color.White
                    )
                }

                // 更多选项按钮 (顶部状态栏适配)
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.3f), MaterialTheme.shapes.small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("在系统文件夹中打开") },
                            onClick = {
                                showMenu = false
                                val currentVideoPath = if (completedVideos.isNotEmpty()) {
                                    completedVideos[pagerState.currentPage].filePath
                                } else {
                                    videoPath
                                }
                                if (currentVideoPath != null) {
                                    viewModel.openInFolder(context, currentVideoPath)
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("使用系统播放器播放") },
                            onClick = {
                                showMenu = false
                                val currentVideoPath = if (completedVideos.isNotEmpty()) {
                                    completedVideos[pagerState.currentPage].filePath
                                } else {
                                    videoPath
                                }
                                
                                if (currentVideoPath != null) {
                                    val videoUri = if (currentVideoPath.startsWith("content://")) {
                                        Uri.parse(currentVideoPath)
                                    } else {
                                        Uri.fromFile(java.io.File(currentVideoPath))
                                    }
                                    
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(videoUri, "video/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "选择播放器"))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
