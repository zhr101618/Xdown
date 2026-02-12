package com.example.xdownloader.presentation.viewmodel;

import com.example.xdownloader.domain.usecase.GetVideoInfoUseCase;
import com.example.xdownloader.domain.usecase.ParseXLinkUseCase;
import com.example.xdownloader.domain.usecase.StartDownloadUseCase;
import com.example.xdownloader.service.DownloadManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider;

  private final Provider<GetVideoInfoUseCase> getVideoInfoUseCaseProvider;

  private final Provider<StartDownloadUseCase> startDownloadUseCaseProvider;

  private final Provider<DownloadManager> downloadManagerProvider;

  public HomeViewModel_Factory(Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider,
      Provider<GetVideoInfoUseCase> getVideoInfoUseCaseProvider,
      Provider<StartDownloadUseCase> startDownloadUseCaseProvider,
      Provider<DownloadManager> downloadManagerProvider) {
    this.parseXLinkUseCaseProvider = parseXLinkUseCaseProvider;
    this.getVideoInfoUseCaseProvider = getVideoInfoUseCaseProvider;
    this.startDownloadUseCaseProvider = startDownloadUseCaseProvider;
    this.downloadManagerProvider = downloadManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(parseXLinkUseCaseProvider.get(), getVideoInfoUseCaseProvider.get(), startDownloadUseCaseProvider.get(), downloadManagerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<ParseXLinkUseCase> parseXLinkUseCaseProvider,
      Provider<GetVideoInfoUseCase> getVideoInfoUseCaseProvider,
      Provider<StartDownloadUseCase> startDownloadUseCaseProvider,
      Provider<DownloadManager> downloadManagerProvider) {
    return new HomeViewModel_Factory(parseXLinkUseCaseProvider, getVideoInfoUseCaseProvider, startDownloadUseCaseProvider, downloadManagerProvider);
  }

  public static HomeViewModel newInstance(ParseXLinkUseCase parseXLinkUseCase,
      GetVideoInfoUseCase getVideoInfoUseCase, StartDownloadUseCase startDownloadUseCase,
      DownloadManager downloadManager) {
    return new HomeViewModel(parseXLinkUseCase, getVideoInfoUseCase, startDownloadUseCase, downloadManager);
  }
}
