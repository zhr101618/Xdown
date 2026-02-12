# X视频下载器 - Android App

一款用于下载X(Twitter)视频的Android应用。

## 功能特性

- **链接解析**: 粘贴X视频分享链接，自动解析视频信息
- **质量选择**: 支持多种视频质量选择（360p, 720p, 1080p等）
- **下载管理**: 支持暂停、继续、取消下载
- **下载列表**: 查看所有下载任务和下载进度
- **视频播放**: 在App内播放已下载的视频
- **自动保存**: 下载的视频自动保存到系统相册

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM
- **依赖注入**: Hilt
- **网络请求**: Retrofit + OkHttp
- **数据存储**: Room Database
- **视频播放**: ExoPlayer

## 项目结构

```
com.example.xdownloader/
├── data/                          # 数据层
│   ├── api/                       # API接口和实现
│   ├── local/                     # 本地存储（数据库、媒体库）
│   ├── model/                     # 数据模型
│   └── repository/                # 数据仓库
├── domain/                        # 领域层
│   ├── usecase/                   # 业务用例
│   └── repository/                # 仓库接口
├── presentation/                  # 表现层
│   ├── ui/                        # UI界面
│   │   ├── screen/                # 页面
│   │   ├── components/            # 可复用组件
│   │   └── theme/                # 主题
│   └── viewmodel/                 # ViewModel
└── service/                       # 服务层
```

## 开发环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.4

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/xdownloader.git
cd xdownloader
```

### 2. 配置local.properties

在项目根目录下创建或编辑 `local.properties` 文件，设置Android SDK路径：

```
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### 3. 打开项目

使用Android Studio打开项目，等待Gradle同步完成。

### 4. 运行项目

连接你的Android设备（红米手机），点击运行按钮即可安装应用。

## 使用说明

1. **粘贴链接**: 在首页输入框粘贴X视频分享链接
2. **解析视频**: 点击"解析"按钮获取视频信息
3. **选择质量**: 选择你想要下载的视频质量
4. **开始下载**: 点击"开始下载"按钮
5. **管理下载**: 在下载列表中查看和管理下载任务
6. **播放视频**: 点击已完成的视频即可播放

## 主要界面

### 首页 (HomeScreen)
- 输入X视频链接
- 粘贴按钮
- 解析视频信息
- 选择视频质量

### 下载列表 (DownloadListScreen)
- 查看所有下载任务
- 显示下载进度
- 暂停/继续下载
- 取消/删除任务
- 播放已下载视频

### 视频播放 (VideoPlayerScreen)
- 全屏播放
- 播放/暂停控制
- 进度条拖动
- 音量调节

## 注意事项

1. **X链接格式**: 支持以下格式
   - `https://x.com/user/status/视频ID`
   - `https://twitter.com/user/status/视频ID`

2. **存储位置**: 下载的视频保存到
   - Android 10+: `Movies/XDownloader/`
   - Android 10以下: `Movies/XDownloader/`

3. **网络权限**: 应用需要网络权限来下载视频

4. **API服务**: 当前使用第三方API服务，可能存在稳定性问题

## 权限说明

应用需要以下权限：

- `INTERNET` - 网络访问
- `ACCESS_NETWORK_STATE` - 网络状态检查
- `WRITE_EXTERNAL_STORAGE` - 写入存储（Android 10以下）
- `READ_EXTERNAL_STORAGE` - 读取存储（Android 10以下）
- `FOREGROUND_SERVICE` - 前台服务
- `POST_NOTIFICATIONS` - 通知权限

## 开发者信息

如有问题或建议，欢迎提Issue。
