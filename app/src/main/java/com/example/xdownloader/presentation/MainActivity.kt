package com.example.xdownloader.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.xdownloader.presentation.ui.screen.DownloadListScreen
import com.example.xdownloader.presentation.ui.screen.HomeScreen
import com.example.xdownloader.presentation.ui.screen.VideoPlayerScreen
import com.example.xdownloader.presentation.ui.theme.XDownloaderTheme
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            XDownloaderTheme {
                // 请求必要权限
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { }

                LaunchedEffect(Unit) {
                    launcher.launch(permissions)
                }

                XDownloaderNavigation()
            }
        }
    }
}

@Composable
fun XDownloaderNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToDownloads = {
                    navController.navigate("downloads") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        composable("downloads") {
            DownloadListScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToPlayer = { videoPath, index ->
                    val encodedPath = java.net.URLEncoder.encode(videoPath, "UTF-8")
                    navController.navigate("player/$encodedPath/$index")
                }
            )
        }

        composable(
            "player/{videoPath}/{index}"
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("videoPath") ?: ""
            val videoPath = java.net.URLDecoder.decode(encodedPath, "UTF-8")
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            VideoPlayerScreen(
                videoPath = videoPath,
                initialIndex = index,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
